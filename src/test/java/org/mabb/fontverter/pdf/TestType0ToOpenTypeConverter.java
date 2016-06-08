/*
 * Copyright (C) Matthew Abboud 2016
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OtfNameConstants.RecordType;
import org.mabb.fontverter.opentype.SfntHeader;

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

    @Test
    public void given_type0_withCFF_convertToOtf_thenSfntHeaderIsCffFlavor() throws Exception {
        PDFont rawType0Font = extractFont(doc, "ZGBKQN+HelveticaNeue-Bold-Identity-H");
        OpenTypeFont font = (OpenTypeFont) PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);

        Assert.assertEquals(SfntHeader.CFF_FLAVOR, font.getSfntHeader().sfntFlavor);
    }

    @Test
    public void given_type0_withCFF_HelveticaNeueBug() throws Exception {
        PDFont rawType0Font = extractFont(doc, "TCQDAA+HelveticaNeue-Light-Identity-H");
        OpenTypeFont font = (OpenTypeFont) PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);
        TestUtils.saveTempFile(font.getData(), "TCQDAA+HelveticaNeue-Light-Identity-H.ttf");

//        Assert.assertEquals(41, font.getCmap().getGlyphMappings().size());
    }

    @Test
    public void given_type0_withCFF_convertToOtf_thenCmapSameNumberOfEntries() throws Exception {
        PDFont rawType0Font = extractFont(doc, "ZGBKQN+HelveticaNeue-Bold-Identity-H");
        OpenTypeFont font = (OpenTypeFont) PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);

        Assert.assertEquals(41, font.getCmap().getGlyphMappings().size());
    }

    @Test
    public void given_type0_withCFF_convertToOtf_then_hmtx_advanced_widths_count_sameAsGlyphCount() throws Exception {
        PDFont rawType0Font = extractFont(doc, "ZGBKQN+HelveticaNeue-Bold-Identity-H");
        OpenTypeFont font = (OpenTypeFont) PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);

        TestUtils.saveTempFile(font.getData(), "ZGBKQN+HelveticaNeue-Bold-Identity-H.otf");
        Assert.assertEquals(42, font.getHmtx().getAdvanceWidths().length);
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
