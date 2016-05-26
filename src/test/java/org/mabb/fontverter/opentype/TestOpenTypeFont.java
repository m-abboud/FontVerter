package org.mabb.fontverter.opentype;

import org.apache.commons.io.FileUtils;
import org.apache.fontbox.ttf.OTFParser;
import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestUtils;

import java.io.File;
import java.io.IOException;

public class TestOpenTypeFont {
    @Test
    public void given_OTF_without_a_OS2WindowsMetricsTable_strictValidatorFails() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "Missing-OS2WinTable.otf");
        Assert.assertFalse(font.doesPassStrictValidation());
    }

    @Test
    public void given_valid_OTF_strictValidatorPasses() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");
        Assert.assertTrue(font.doesPassStrictValidation());
    }

    @Test
    public void given_OTF_without_a_OS2WindowsMetricsTable_normalizeAddsOne() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "Missing-OS2WinTable.otf");
        font.normalize();

        OpenTypeFont otfFont = ((OtfFontAdapter)font).getUnderlyingFont();
        Assert.assertTrue(font.doesPassStrictValidation());
        Assert.assertTrue(otfFont.getOs2() != null);

        FileUtils.writeByteArrayToFile(new File(TestUtils.tempOutputPath + "Fixed-Missing-OS2WinTable.otf"), font.getData());
    }

    @Test
    public void parseOtf_thenRegenerateFontData_FontDataIsSameLength() throws IOException {
        File file = new File(TestUtils.TEST_PATH + "FontVerter+FullAlphabetFont.otf");
        byte[] data = FileUtils.readFileToByteArray(file);
        FVFont font = FontVerter.readFont(data);
        Assert.assertEquals(data.length, font.getData().length);

        FileUtils.writeByteArrayToFile(new File(TestUtils.tempOutputPath + "Regen-FullAlphabetFont.otf"), font.getData());
    }
}
