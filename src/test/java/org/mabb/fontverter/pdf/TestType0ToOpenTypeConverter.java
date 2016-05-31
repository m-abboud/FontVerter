package org.mabb.fontverter.pdf;

import org.apache.commons.io.FileUtils;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.CharsetConverter;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OpenTypeFont;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestType0ToOpenTypeConverter {
    @Test
    public void given_type0With_TTF_descendant_WithNoCmapTable_whenConverted_thenCmapTableCreated()
            throws IOException {
        PDFont rawType0Font = extractFont("pdf/HorariosMadrid_Segovia.pdf", "UMAVUG+Garuda-Identity-H");

        FVFont font = PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);
        font.normalize();
        OpenTypeFont otfFont = ((OpenTypeFont) font);

        Assert.assertNotNull(otfFont.getCmap());
        Assert.assertEquals(otfFont.getCmap().getGlyphCount(), 69);

        FileUtils.writeByteArrayToFile(new File("C:\\projects\\Pdf2Dom\\UMAVUG+Garuda-Identity-H.ttf"), font.getData());
    }

    private PDFont extractFont(String pdfFile, String name) throws IOException {
        PdfFontExtractor extractor = new PdfFontExtractor();
        PDDocument doc = PDDocument.load(TestUtils.readTestFile(pdfFile));

        List<PDFont> fonts = extractor.extractToPDFBoxFonts(doc);
        return findFont(fonts, name);
    }

    private PDFont findFont(List<PDFont> fonts, String name) {
        PDFont searchFont = null;
        for (PDFont fontOn : fonts)
            if (fontOn.getName().equals(name))
                searchFont = fontOn;

        Assert.assertNotNull(searchFont);
        return searchFont;
    }
}
