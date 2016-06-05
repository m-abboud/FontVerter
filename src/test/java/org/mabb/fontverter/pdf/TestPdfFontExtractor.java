package org.mabb.fontverter.pdf;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestPdfFontExtractor {
    @Test
    public void givenPdfWith2Fonts_extractFontsToFVFontList_thenListHasSameNumberOfFonts() throws IOException {
        PDDocument doc = PDDocument.load(TestUtils.readTestFile("pdf/brno30.pdf"));
        PdfFontExtractor extractor = new PdfFontExtractor();

        List<FVFont> fonts = extractor.extractToFVFonts(doc);

        Assert.assertEquals(3, fonts.size());
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void givenPdfWith2Fonts_extractFontsToDir_thenDirectoryHasThreeTtfFiles() throws IOException {
        PDDocument doc = PDDocument.load(TestUtils.readTestFile("pdf/brno30.pdf"));
        PdfFontExtractor extractor = new PdfFontExtractor();

        File extractDir = folder.getRoot();
        extractor.extractFontsToDir(doc, extractDir);
        File[] fontFiles = extractDir.listFiles();

        Assert.assertEquals(3, fontFiles.length);
        for (File fileOn : fontFiles)
            Assert.assertEquals("ttf", FilenameUtils.getExtension(fileOn.getPath()));
    }

    @Test
    public void givenPdfWith2Fonts_extractFontsToDirWithWoff1FormatSet_thenDirectoryHasThreeWoffFiles() throws IOException {
        File extractDir = folder.getRoot();
        PDDocument doc = PDDocument.load(TestUtils.readTestFile("pdf/brno30.pdf"));

        PdfFontExtractor extractor = new PdfFontExtractor();
        extractor.setExtractFormat(FontVerter.FontFormat.WOFF1);

        extractor.extractFontsToDir(doc, extractDir);
        File[] fontFiles = extractDir.listFiles();

        Assert.assertEquals(3, fontFiles.length);
        for (File fileOn : fontFiles)
            Assert.assertEquals("woff", FilenameUtils.getExtension(fileOn.getPath()));
    }
}
