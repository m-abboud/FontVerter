package org.mabb.fontverter.opentype;

import org.apache.commons.io.FileUtils;
import org.apache.fontbox.ttf.OTFParser;
import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.validator.RuleValidator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mabb.fontverter.validator.RuleValidator.*;

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
    public void given_OTF_without_an_OS2WindowsMetricsTable_thenNormalize_addsOS2Table() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "Missing-OS2WinTable.otf");
        font.normalize();

        OpenTypeFont otfFont = ((OtfFontAdapter) font).getUnderlyingFont();
        Assert.assertTrue(font.doesPassStrictValidation());
        Assert.assertTrue(otfFont.getOs2() != null);

        FileUtils.writeByteArrayToFile(new File(TestUtils.tempOutputPath + "Fixed-Missing-OS2WinTable.otf"), font.getData());
    }

    @Test
    public void parseOtf_thenRegenerateFontData_fontDataIsSameLength() throws IOException {
        File file = new File(TestUtils.TEST_PATH + "FontVerter+FullAlphabetFont.otf");
        byte[] data = FileUtils.readFileToByteArray(file);
        FVFont font = FontVerter.readFont(data);

        Assert.assertEquals(data.length, font.getData().length);

        FileUtils.writeByteArrayToFile(new File(TestUtils.tempOutputPath + "Regen-FullAlphabetFont.otf"), font.getData());
    }

    @Test
    public void given_TTF_MissingPostScriptTable_strictValidatorFails() throws IOException, IllegalAccessException, InstantiationException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        Assert.assertNotNull(findErrorContaining(font, "postscript"));
    }

    @Test
    public void given_TTF_MissingPostScriptTable_normalizeAddsOne() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont otfFont = normalizeFont("ttf/GKQXJT+Timetable.ttf");

        Assert.assertNotNull(otfFont.getPost());
        Assert.assertEquals(2F, otfFont.getPost().getVersion(), 0);
    }

    @Test
    public void given_TTF_MissingNameTable_strictValidatorFails() throws IOException, IllegalAccessException, InstantiationException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        Assert.assertNotNull(findErrorContaining(font, "name"));
    }

    @Test
    public void given_TTF_MissingNameTable_normalizeAddsOne() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont otfFont = normalizeFont("ttf/GKQXJT+Timetable.ttf");

        Assert.assertNotNull(otfFont.getNameTable());
    }

    @Test
    public void given_TTF_withUnevenCvtTableLength_strictValidatorFails() throws IOException, IllegalAccessException, InstantiationException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        Assert.assertNotNull(findErrorContaining(font, "cvt "));
    }

    private FontValidatorError findErrorContaining(FVFont font, String containing) {
        Assert.assertFalse(font.doesPassStrictValidation());
        List<FontValidatorError> errors = font.getStrictValidationErrors();

        FontValidatorError findError = null;
        for (FontValidatorError errorOn : errors)
            if (errorOn.getMessage().toLowerCase().contains(containing))
                findError = errorOn;

        return findError;
    }

    private OpenTypeFont normalizeFont(String fontFile) throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + fontFile);
        font.normalize();
        return ((OtfFontAdapter) font).getUnderlyingFont();
    }

}
