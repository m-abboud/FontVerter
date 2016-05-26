package org.mabb.fontverter;

import org.apache.commons.io.FileUtils;
import org.mabb.fontverter.cff.CFFToOpenTypeConverter;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.opentype.OtfNameConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mabb.fontverter.opentype.OtfNameConstants.*;
import static org.mabb.fontverter.opentype.OtfNameConstants.RecordType.*;

public class TestCFFToOtfConverter {

    @Test
    public void convertSimpleFont_fontValidatorsPass() throws Exception {
        OpenTypeFont font = convertAndSaveFile("cff/FontVerter+SimpleTestFont");
        TestUtils.runAllValidators(font);
    }

    @Test
    public void convertFullAlphabetFont_fontValidatorsPass() throws Exception {
        OpenTypeFont font = convertAndSaveFile("cff/FontVerter+FullAlphabetFont");
        TestUtils.runAllValidators(font);
    }

    @Test
    public void convert_CFF_fullAlphabetFont_then_OTF_has94Glyphs() throws Exception {
        OpenTypeFont font = convert("cff/FontVerter+FullAlphabetFont");
        // getNumGlyphs includes padded glyph so -1 for padding,there's 4 actual glyphs
        Assert.assertEquals(94, font.getCmap().getGlyphCount() - 1);
    }

    @Test
    public void convert_CFF_withDictNameEntries_Then_OTF_hasFontNameRecordsSet() throws Exception {
        OpenTypeFont font = convert("cff/FontVerter+SimpleTestFont");

        Assert.assertEquals("SimpleTestFont", font.getNameTable().getName(FONT_FAMILY));
        Assert.assertEquals("FontVerter+SimpleTestFont", font.getNameTable().getName(FULL_FONT_NAME));
        Assert.assertEquals("Medium", font.getNameTable().getName(FONT_SUB_FAMILY));
    }

    @Test
    public void convertCFF_OutputHasDescenderValue() throws Exception {
        OpenTypeFont font = convert("cff/FontVerter+SimpleTestFont");

        Assert.assertEquals(-133, font.getHhea().descender);
    }

    @Test
    public void convertCFF_OutputHasBoundingBox() throws Exception {
        OpenTypeFont font = convert("cff/FontVerter+SimpleTestFont");

        Assert.assertEquals(26, font.head.getxMin());
        Assert.assertEquals(-2, font.head.getyMin());
        Assert.assertEquals(1297, font.head.getxMax());
        Assert.assertEquals(793, font.head.getyMax());
    }


    @Test
    public void convertCffWithEmptyVersion_givesOtfWithFillerVersion() throws Exception {
        OpenTypeFont font = convert("cff/DCKDHE+Omsym6");

        Assert.assertEquals("Version 1.1", font.getNameTable().getName(RecordType.VERSION_STRING));
    }

    @Test
    public void convertAndValidateAllTestCffFonts() throws Exception {
        File dir = new File(TestUtils.TEST_PATH + "cff/");
        List<File> cffFiles = (List<File>) FileUtils.listFiles(dir, new String[]{"cff"}, true);
        Assert.assertTrue(cffFiles.size() > 2);

        for (File file : cffFiles) {
            OpenTypeFont font = convertAndSaveFile(file);
            TestUtils.runAllValidators(font);
        }
    }


    public static OpenTypeFont convertAndSaveFile(String fileName) throws Exception {
        return convertAndSaveFile(new File(TestUtils.TEST_PATH + fileName + ".cff"));
    }

    public static OpenTypeFont convertAndSaveFile(File file) throws Exception {
        File outputFile = new File(TestUtils.tempOutputPath + file.getName().replace(".cff", ".otf"));
        if (outputFile.exists())
            outputFile.delete();

        OpenTypeFont generatedFont = convert(file);
        byte[] fontData = generatedFont.getFontData();
        FileUtils.writeByteArrayToFile(outputFile, fontData);
        generatedFont.setSourceFile(outputFile);

        return generatedFont;
    }

    private static OpenTypeFont convert(String fileName) throws IOException {
        return convert(new File(TestUtils.TEST_PATH + fileName + ".cff"));
    }

    private static OpenTypeFont convert(File file) throws IOException {
        byte[] cff = FileUtils.readFileToByteArray(file);
        CFFToOpenTypeConverter gen = new CFFToOpenTypeConverter(cff);
        return gen.generateFont();
    }
}
