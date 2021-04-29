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

package org.mabb.fontverter.converter;

import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;

import static org.mabb.fontverter.TestUtils.TEST_PATH;
import static org.mabb.fontverter.TestUtils.saveTempFile;

public class TestWoffToOtfConverter {

    @Test
    public void convertWoff1_toOtf_validatorPasses() throws Exception {
        FVFont otfFont = FontVerter.convertFont(TEST_PATH + "Open-Sans-WOFF-1.0.woff", FontVerter.FontFormat.OTF);
        byte[] fontData = otfFont.getData();
        saveTempFile(fontData, "Open-Sans-WOFF-1.0.otf");

        Assert.assertTrue(otfFont.isValid());
    }


    // Woff2 de compress broken, woff2->otf not supported yet
//    @Test
//    public void convertWoff2_toOtf_validatorPasses() throws Exception {
//        FVFont otfFont = FontVerter.convertFont(TEST_PATH + "Open-Sans-WOFF-2.0.woff2", FontVerter.FontFormat.OTF);
//        byte[] fontData = otfFont.getData();
//        saveTempFile(fontData, "Open-Sans-WOFF-2.0.otf");
//
//        Assert.assertTrue(otfFont.isValid());
//    }
//
//    @Test
//    public void convertWoff2_toOtf_thenOtfHasSameNumberOfTablesAsWoff() throws Exception {
//        FVFont font = FontVerter.convertFont(TEST_PATH + "Open-Sans-WOFF-2.0.woff2", FontVerter.FontFormat.OTF);
//        OpenTypeFont otfFont = (OpenTypeFont) font;
//
//        Assert.assertEquals(17, otfFont.getTables().size());
//    }
//
//    @Test
//    public void convertWoff2_toOtf_thenOtfSfntFlavor_calculatedCorrectly() throws Exception {
//        FVFont font = FontVerter.convertFont(TEST_PATH + "Open-Sans-WOFF-2.0.woff2", FontVerter.FontFormat.OTF);
//        OpenTypeFont otfFont = (OpenTypeFont) font;
//
//        Assert.assertEquals(SfntHeader.VERSION_1, otfFont.getSfntHeader().sfntFlavor);
//    }
}
