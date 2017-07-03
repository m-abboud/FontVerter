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

package org.mabb.fontverter.opentype;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontProperties;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.mabb.fontverter.validator.RuleValidator.*;

public class TestOpenTypeFont {
    @Test
    public void given_OTF_without_a_OS2WindowsMetricsTable_strictValidatorFails() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "Missing-OS2WinTable.otf");
        Assert.assertFalse(font.isValid());
    }

    @Test
    public void given_valid_OTF_strictValidatorPasses() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");
        Assert.assertTrue(font.isValid());
    }

    @Test
    public void given_OTF_without_an_OS2WindowsMetricsTable_thenNormalize_addsOS2Table() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "Missing-OS2WinTable.otf");
        font.normalize();

        OpenTypeFont otfFont = ((OpenTypeFont) font);
        Assert.assertTrue(font.isValid());
        Assert.assertTrue(otfFont.getOs2() != null);

        FileUtils.writeByteArrayToFile(new File(TestUtils.tempOutputPath + "Fixed-Missing-OS2WinTable.otf"), font.getData());
    }

    @Test
    public void given_OTF_cffTypeFont_fileEndingIsOtf() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");
        Assert.assertEquals(font.getProperties().getFileEnding(), "otf");
    }

    @Test
    public void given_TTF_MissingPostScriptTable_strictValidatorFails() throws Exception {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        Assert.assertNotNull(findErrorContaining(font, "postscript"));
    }

    @Test
    public void given_TTF_MissingPostScriptTable_normalizeAddsOne() throws Exception {
        OpenTypeFont otfFont = normalizeFont("ttf/GKQXJT+Timetable.ttf");

        Assert.assertNotNull(otfFont.getPost());
        Assert.assertEquals(1F, otfFont.getPost().getVersion(), 0);
    }

    @Test
    public void given_TTF_MissingNameTable_strictValidatorFails() throws Exception {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        Assert.assertNotNull(findErrorContaining(font, "name"));
    }

    @Test
    public void given_TTF_MissingNameTable_normalizeAddsOne() throws Exception {
        OpenTypeFont otfFont = normalizeFont("ttf/GKQXJT+Timetable.ttf");

        Assert.assertNotNull(otfFont.getNameTable());
    }

    @Test
    public void given_TTF_postScriptType_fileEndingIsTtf() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        Assert.assertEquals(font.getProperties().getFileEnding(), "ttf");
    }

    @Test
    public void given_TTF_withUnevenCvtTableLength_strictValidatorFails() throws Exception {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        Assert.assertNotNull(findErrorContaining(font, "cvt "));
    }

    @Test
    public void given_OTF_cfftype_whenRead_fontNameIsValid() throws Exception {
        // for github issue #4
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "NameReadTestFont.otf");
        Assert.assertThat(font.getName(), containsString("Avenir"));
    }

    @Test
    public void given_OTF_cffTypeFont_thenPropertyNamesValid() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+FullAlphabetFont.otf");
        FontProperties properties = font.getProperties();

        Assert.assertEquals(properties.getFullName(), "Full Alphabet Font");
        Assert.assertEquals("FontVerter+FullAlphabetFont", properties.getName());
        Assert.assertEquals("None of your business!", properties.getSubFamilyName());
        Assert.assertEquals("001.000", properties.getVersion());
        Assert.assertEquals("it's a test font! i dunno MIT license or something ", properties.getTrademarkNotice());
    }

    @Test
    public void given_TTF_whenRead_thenPropertyNamesValid() throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + "KJJTAM+TrebuchetMS.ttf");
        FontProperties properties = font.getProperties();

        Assert.assertEquals("Trebuchet MS", properties.getFullName());
        Assert.assertEquals("Trebuchet MS", properties.getName());
        Assert.assertEquals("Regular", properties.getSubFamilyName());
        Assert.assertEquals("Version 1.15", properties.getVersion());
        Assert.assertEquals("Copyright (c) 1996 Microsoft Corporation. All rights reserved.", properties.getTrademarkNotice());
    }

    private FontValidatorError findErrorContaining(FVFont font, String containing) {
        Assert.assertFalse(font.isValid());
        List<FontValidatorError> errors = font.getValidationErrors();

        FontValidatorError findError = null;
        for (FontValidatorError errorOn : errors)
            if (errorOn.getMessage().toLowerCase().contains(containing))
                findError = errorOn;

        return findError;
    }

    private OpenTypeFont normalizeFont(String fontFile) throws IOException {
        FVFont font = FontVerter.readFont(TestUtils.TEST_PATH + fontFile);
        font.normalize();
        return ((OpenTypeFont) font);
    }

}
