/*
 * Copyright (C) Maddie Abboud 2016
 *
 * FontVerter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FontVerter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FontVerter. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mabb.fontverter.pdf;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.FontVerter.FontFormat;
import org.mabb.fontverter.converter.PsType0ToOpenTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility to extract all fonts in a given PDF
 */
public class PdfFontExtractor extends PDFTextStripper {
    private static final String[] HELP_CODES = new String[]{"-h", "help", "--help", "/?"};
    private static final String DEFAULT_EXTRACT_PATH = "fonts/";

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || Arrays.asList(HELP_CODES).contains(args[0])) {
            System.out.println("Usage: PdfFontExtractor <pdf file or directory containing pdf files>");
            System.out.println("Options: ");
            System.out.println("-ff=<format> Font format to save the fonts as. <format> = WOFF1, WOFF2, OTF. Defaults to OTF.");
            System.out.println("-dir=<path> Directory to extract to. <path> = font extract directory. Defaults to {current dir}/fonts/");

            System.exit(1);
        }

        String extractPath = DEFAULT_EXTRACT_PATH;
        FontFormat format = FontFormat.OTF;

        for (String argOn : args) {
            String value = argOn.replaceAll("-[^=]*=", "");

            if (argOn.startsWith("-ff="))
                format = FontFormat.fromString(value);
            else if (argOn.startsWith("-dir="))
                extractPath = value;
        }

        File pdf = new File(args[0]);
        if (pdf.isDirectory()) {
            List<File> pdfFiles = (List<File>) FileUtils.listFiles(pdf, new String[]{"pdf"}, true);

            for (File fileOn : pdfFiles)
                try {
                    extractPdfFonts(extractPath, fileOn, format);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
        } else
            extractPdfFonts(extractPath, pdf, format);
    }

    private static void extractPdfFonts(String extractPath, File pdfFile, FontFormat format) throws IOException {
        File fontExtractDir = new File(extractPath);
        if (!fontExtractDir.exists())
            fontExtractDir.mkdir();

        PDDocument pdf = Loader.loadPDF(pdfFile);

        PdfFontExtractor fontExtractor = new PdfFontExtractor();
        fontExtractor.setExtractFormat(format);
        fontExtractor.extractFontsToDir(pdf, extractPath);

        pdf.close();
    }

    private FontFormat extractFormat = FontFormat.OTF;
    private static Logger log = LoggerFactory.getLogger(PdfFontExtractor.class);
    private PDPage pdpage;
    private ExtractFontStrategy extractStrategy;

    public PdfFontExtractor() throws IOException {
        super();
    }

    public void extractFontsToDir(File pdf, String path) throws IOException {
        PDDocument doc = Loader.loadPDF(pdf);
        extractFontsToDir(doc, path);
        doc.close();
    }

    public void extractFontsToDir(byte[] pdf, String path) throws IOException {
        PDDocument doc = Loader.loadPDF(pdf);
        extractFontsToDir(doc, path);
        doc.close();
    }

    public void extractFontsToDir(PDDocument pdf, File path) throws IOException {
        extractFontsToDir(pdf, path.getPath() + "/");
    }

    public void extractFontsToDir(PDDocument pdf, String path) throws IOException {
        this.extractStrategy = new ExtractToDirStrategy(path, extractFormat);
        Writer output = new StringWriter();
        writeText(pdf, output);

        output.close();
    }

    public List<FVFont> extractToFVFonts(PDDocument pdf) throws IOException {
        this.extractStrategy = new ExtractFVFontStrategy();
        Writer output = new StringWriter();
        writeText(pdf, output);
        output.close();

        return ((ExtractFVFontStrategy) extractStrategy).extractedFvFonts;
    }

    public List<PDFont> extractToPDFBoxFonts(PDDocument pdf) throws IOException {
        this.extractStrategy = new ExtractToPDFBoxFontStrategy();
        Writer output = new StringWriter();
        writeText(pdf, output);
        output.close();

        return ((ExtractToPDFBoxFontStrategy) extractStrategy).extractedFonts;
    }

    public void processPage(PDPage page) throws IOException {
        pdpage = page;
        tryExtractPageFonts();
        super.processPage(page);
    }

    protected void tryExtractPageFonts() {
        PDResources resources = pdpage.getResources();
        if (resources == null)
            return;

        try {
            extractFontResources(resources);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractFontResources(PDResources resources) throws IOException {
        for (COSName key : resources.getFontNames()) {
            PDFont font = resources.getFont(key);
            extractStrategy.extract(font);
        }

        for (COSName name : resources.getXObjectNames()) {
            PDXObject xobject = resources.getXObject(name);
            if (xobject instanceof PDFormXObject) {
                PDFormXObject xObjectForm = (PDFormXObject) xobject;
                PDResources formResources = xObjectForm.getResources();

                if (formResources != null)
                    extractFontResources(formResources);
            }
        }
    }

    public static FVFont convertFont(PDFont font, FontFormat format) throws IOException {
        FVFont readFont = null;

        if (font instanceof PDTrueTypeFont) {
            if (font.getFontDescriptor() == null || font.getFontDescriptor().getFontFile2() == null) {
                log.warn("Skipped font: '{}'. No font file 2 found", font.getName());
                return null;
            }

            byte[] data = font.getFontDescriptor().getFontFile2().toByteArray();
            readFont = FontVerter.readFont(data);

        } else if (font instanceof PDType0Font) {
            readFont = convertType0FontToOpenType((PDType0Font) font);
            readFont.normalize();

        } else if (font instanceof PDType1CFont) {
            byte[] data = font.getFontDescriptor().getFontFile3().toByteArray();
            readFont = FontVerter.readFont(data);

        } else
            log.warn("Skipped font: '{}'. FontVerter does not support font type: '{}'", font.getName(), font.getType());

        if (readFont == null)
            return null;

        if (!readFont.isValid())
            readFont.normalize();

        return FontVerter.convertFont(readFont, format);
    }

    public static FVFont convertType0FontToOpenType(PDType0Font font) throws IOException {
        PsType0ToOpenTypeConverter converter = new PsType0ToOpenTypeConverter();
        try {
            return converter.convert(font);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public FontFormat getExtractFormat() {
        return extractFormat;
    }

    public void setExtractFormat(FontFormat extractFormat) {
        this.extractFormat = extractFormat;
    }

    static abstract class ExtractFontStrategy {
        protected List<PDFont> extractedFonts = new ArrayList<PDFont>();

        public abstract void extract(PDFont font) throws IOException;

        protected boolean hasExtractedFont(PDFont font) {
            for (PDFont fontOn : extractedFonts)
                if (fontOn.getName().equals(font.getName()) && fontOn.getClass() == font.getClass())
                    return true;

            return false;
        }
    }

    static class ExtractToPDFBoxFontStrategy extends ExtractFontStrategy {
        public void extract(PDFont font) throws IOException {
            extractedFonts.add(font);
        }
    }

    static class ExtractFVFontStrategy extends ExtractFontStrategy {
        private List<FVFont> extractedFvFonts = new ArrayList<FVFont>();

        public void extract(PDFont font) throws IOException {
            extractedFonts.add(font);
            FVFont convertedFont = convertFont(font, FontFormat.OTF);

            if (convertedFont != null)
                extractedFvFonts.add(convertedFont);

            log.info("Extracted: {}", font.getName());
        }
    }

    static class ExtractToDirStrategy extends ExtractFontStrategy {
        private String extractPath;
        private final FontFormat format;

        public ExtractToDirStrategy(String path, FontFormat format) {
            extractPath = path;
            this.format = format;
        }

        public void extract(PDFont font) throws IOException {
            if (hasExtractedFont(font))
                return;
            else
                extractedFonts.add(font);

            FVFont saveFont = convertFont(font, format);

            if (saveFont != null && !extractPath.isEmpty()) {
                String fileEnding = saveFont.getProperties().getFileEnding();
                if (!extractPath.endsWith("/"))
                    extractPath += "/";

                String fontFilePath = extractPath + font.getName() + "." + fileEnding;

                FileUtils.writeByteArrayToFile(new File(fontFilePath), saveFont.getData());

                log.info("Extracted: {}", fontFilePath);
            }
        }
    }
}
