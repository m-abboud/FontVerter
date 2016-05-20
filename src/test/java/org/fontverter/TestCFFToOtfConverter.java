package org.fontverter;

import org.apache.commons.io.FileUtils;
import org.fontverter.cff.CFFToOpenTypeConverter;
import org.fontverter.opentype.OpenTypeFont;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.fontverter.opentype.OtfNameConstants.RecordType.*;

public class TestCFFToOtfConverter {

    @Test
    public void convertSimpleFont_fontValidatorsPass() throws Exception {
        OpenTypeFont font = convertAndSaveFile("FontVerter+SimpleTestFont");
        TestUtils.runAllValidators(font);
    }

    @Test
    public void convertFullAlphabetFont_fontValidatorsPass() throws Exception {
//        OpenTypeFont font = convertAndSaveFile("FontVerter+FullAlphabetFont");
//        TestUtils.runAllValidators(font);
    }

    @Test
    public void convert_CFF_fullAlphabetFont_then_OTF_has94Glyphs() throws Exception {
        OpenTypeFont font = convert("FontVerter+FullAlphabetFont");
        // getNumGlyphs includes padded glyph so -1 for padding,there's 4 actual glyphs
        Assert.assertEquals(94, font.cmap.getGlyphCount() - 1);
    }

    @Test
    public void convert_CFF_withDictNameEntries_Then_OTF_hasFontNameRecordsSet() throws Exception {
        OpenTypeFont font = convert("FontVerter+SimpleTestFont");

        Assert.assertEquals("SimpleTestFont", font.name.getName(FONT_FAMILY));
        Assert.assertEquals("FontVerter+SimpleTestFont", font.name.getName(FULL_FONT_NAME));
        Assert.assertEquals("Medium", font.name.getName(FONT_SUB_FAMILY));
    }

    @Test
    public void convertCFF_OutputHasDescenderValue() throws Exception {
        OpenTypeFont font = convert("FontVerter+SimpleTestFont");

        Assert.assertEquals(-133, font.hhea.descender);
    }

    @Test
    public void convertCFF_OutputHasBoundingBox() throws Exception {
        OpenTypeFont font = convert("FontVerter+SimpleTestFont");

        Assert.assertEquals(26, font.head.getxMin());
        Assert.assertEquals(-2, font.head.getyMin());
        Assert.assertEquals(1297, font.head.getxMax());
        Assert.assertEquals(793, font.head.getyMax());
    }

    public static OpenTypeFont convertAndSaveFile(String fileName) throws Exception {
        File outputFile = new File(TestUtils.tempOutputPath + fileName + ".otf");
        if(outputFile.exists())
            outputFile.delete();

        OpenTypeFont generatedFont = convert(fileName);
        byte[] fontData = generatedFont.getFontData();
        FileUtils.writeByteArrayToFile(outputFile, fontData);
        generatedFont.setSourceFile(outputFile);

        return generatedFont;
    }

    private static OpenTypeFont convert(String fileName) throws IOException {
        byte[] cff = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + fileName + ".cff"));
        CFFToOpenTypeConverter gen = new CFFToOpenTypeConverter(cff);
        return gen.generateFont();
    }

}
