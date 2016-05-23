package org.fontverter;

import org.apache.commons.io.FileUtils;
import org.fontverter.cff.CffFontAdapter;
import org.fontverter.opentype.OtfFontAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestFontVerter {
    private static final String tempOutputPath = "src/test/test-output/";

    @Test
    public void givenCffFile_detectsCffFontFormat() throws IOException {
        File file = new File(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.cff");
        FVFont font = FontVerter.readFont(file);

        Assert.assertEquals(CffFontAdapter.class, font.getClass());
    }

    @Test
    public void givenOtfFile_detectsCffFontFormat() throws IOException {
        File file = new File(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");
        FVFont font = FontVerter.readFont(file);

        Assert.assertEquals(OtfFontAdapter.class, font.getClass());
    }

    @Test
    public void givenCffFont_convertWithFontVerterApi_fontValidatorsPass() throws Exception {
        File file = new File(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.cff");
        OtfFontAdapter font = (OtfFontAdapter) FontVerter.convertFont(file, FontVerter.FontFormat.OTF);

        File outputFile = new File(tempOutputPath + "FontVerter+SimpleTestFont.otf");
        if (outputFile.exists())
            outputFile.delete();
        FileUtils.writeByteArrayToFile(outputFile, font.getUnderlyingFont().getFontData());
        font.getUnderlyingFont().setSourceFile(outputFile);

        TestUtils.runAllValidators(font.getUnderlyingFont());
        Assert.assertTrue(font.getUnderlyingFont().getCmap().getGlyphCount() > 3);
    }
}
