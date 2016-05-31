package org.mabb.fontverter.pdf;

import org.apache.commons.io.FileUtils;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.CharsetConverter;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OtfNameConstants;
import org.mabb.fontverter.opentype.OtfNameConstants.RecordType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestType0ToOpenTypeConverter {
    private static PDDocument doc;

    public TestType0ToOpenTypeConverter() throws IOException {
        doc = PDDocument.load(TestUtils.readTestFile("pdf/HorariosMadrid_Segovia.pdf"));
    }

    @After
    public void cleanUp() throws IOException {
        doc.close();
    }

    @Test
    public void given_type0With_TTF_descendant_WithNoCmapTable_whenConverted_thenCmapTableCreated()
            throws IOException {
        PDFont rawType0Font = extractFont(doc, "UMAVUG+Garuda-Identity-H");

        FVFont font = PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);
        font.normalize();
        OpenTypeFont otfFont = ((OpenTypeFont) font);

        Assert.assertNotNull(otfFont.getCmap());
        Assert.assertEquals(otfFont.getMxap().getNumGlyphs(), 69);
    }

    @Test
    public void given_type0_withTTF_withNoNameTable_whenConverted_thenHasNamesSet() throws IOException {
        PDFont rawType0Font = extractFont(doc, "UMAVUG+Garuda-Identity-H");

        FVFont font = PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);
        font.normalize();
        OpenTypeFont otfFont = ((OpenTypeFont) font);

        Assert.assertEquals("UMAVUG+Garuda-Identity-H", otfFont.getNameTable().getName(RecordType.FULL_FONT_NAME));
        Assert.assertEquals("Garuda", otfFont.getNameTable().getName(RecordType.FONT_FAMILY));
        Assert.assertEquals("Normal", otfFont.getNameTable().getName(RecordType.FONT_SUB_FAMILY));
    }

    private PDFont extractFont(PDDocument pdfFile, String name) throws IOException {
        PdfFontExtractor extractor = new PdfFontExtractor();
        List<PDFont> fonts = extractor.extractToPDFBoxFonts(pdfFile);
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
