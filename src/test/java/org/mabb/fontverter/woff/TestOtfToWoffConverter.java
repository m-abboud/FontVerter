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

package org.mabb.fontverter.woff;

import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.FontVerter.FontFormat;
import org.mabb.fontverter.woff.WoffConstants.*;
import org.junit.Assert;
import org.junit.Test;

import static org.mabb.fontverter.TestUtils.*;
import static org.mabb.fontverter.woff.WoffConstants.TableFlagType.*;

public class TestOtfToWoffConverter {
    @Test
    public void convertOtfToWoff1_woffFontHasSameNumberOfTables() throws Exception {
        WoffFont woffFont = (WoffFont) FontVerter.convertFont(TEST_PATH + "FontVerter+SimpleTestFont.otf", FontFormat.WOFF1);
        Assert.assertEquals(9, woffFont.getTables().size());
    }

    @Test
    public void convertOtf_toWoff1_validatorPasses() throws Exception {
        FVFont woffFont = FontVerter.convertFont(TEST_PATH + "FontVerter+SimpleTestFont.otf", FontFormat.WOFF1);
        byte[] fontData = woffFont.getData();
        saveTempFile(fontData, "FontVerter+SimpleTestFont.woff");

        // parse bytes and rebuild font obj for validation so know data is written right
        WoffParser parser = new WoffParser();
        WoffFont parsedFont = parser.parse(fontData);

        Woff1Validator validator = new Woff1Validator();
        validator.validateWithExceptionsThrown((Woff1Font) parsedFont);
    }

    @Test
    public void convertCffToWoff_woffFontHasSameNumberOfTables() throws Exception {
        WoffFont woffFont = (WoffFont) FontVerter.convertFont(TEST_PATH + "cff/test.cff", FontFormat.WOFF1);
        Assert.assertEquals(9, woffFont.getTables().size());
    }

    @Test
    public void convertCff_ToOtf_ToWoff1_validatorPasses() throws Exception {
        FVFont woffFont = FontVerter.convertFont(TEST_PATH + "cff/test.cff", FontFormat.WOFF1);
        byte[] fontData = woffFont.getData();
        saveTempFile(fontData, "FontVerter+SimpleTestFont.woff");

        // parse bytes and rebuild font obj for validation so know data is written right
        WoffParser parser = new WoffParser();
        WoffFont parsedFont = parser.parse(fontData);

        Woff1Validator validator = new Woff1Validator();
        validator.validateWithExceptionsThrown((Woff1Font) parsedFont);
    }

    @Test
    public void convertCff_ToOtf_ToWoff2_validatorPasses() throws Exception {
        WoffFont parsedFont = convertAndReparseWoff2("cff/test.cff");

        Woff2Validator validator = new Woff2Validator();
        validator.validateWithExceptionsThrown((Woff2Font) parsedFont);
    }

    @Test
    public void convertCffToWoff2_thenAllOtfTableTypes_arePresentInWoff2Font() throws Exception {
        WoffFont parsedFont = convertAndReparseWoff2("cff/test.cff");

        TableFlagType[] flags = new TableFlagType[]{CFF, post, OS2, head, hmtx, cmap, name, hhea, maxp};
        for (TableFlagType flagOn : flags) {
            boolean flagFound = false;
            for (WoffTable table : parsedFont.getTables()) {
                if (flagOn == ((Woff2Font.Woff2Table)table).flag)
                    flagFound = true;
            }

            Assert.assertTrue(String.format("flag %s type not found", flagOn), flagFound);
        }
    }

    @Test
    public void convertCffToWoff2_sfntSizeSameAsOtfFontSize() throws Exception {
        WoffFont convertedFont = (WoffFont) FontVerter.convertFont(TEST_PATH + "cff/test.cff", FontFormat.WOFF2);
        int otfLength = convertedFont.getFonts().get(0).getData().length;

        Woff2Parser parser = new Woff2Parser();
        WoffFont reparsedFont = parser.parse(convertedFont.getData());

        Assert.assertEquals(otfLength, reparsedFont.header.totalSfntSize);
    }

    private WoffFont convertAndReparseWoff2(String cffFile) throws Exception {
        Woff2Font convertedFont = (Woff2Font) FontVerter.convertFont(TEST_PATH + cffFile, FontFormat.WOFF2);
        byte[] fontData = convertedFont.getData();

        saveTempFile(fontData, "FontVerter+SimpleTestFont.woff2");

        // parse bytes and rebuild font obj for validation so know data is written right
        Woff2Parser parser = new Woff2Parser();

        // readd underlying opentype font for validator
        Woff2Font reparseFont = (Woff2Font) parser.parse(fontData);
        reparseFont.getFonts().add(convertedFont.fonts.get(0));
        return convertedFont;
    }
}
