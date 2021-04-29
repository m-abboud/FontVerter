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

package org.mabb.fontverter.woff;

import org.apache.commons.io.FileUtils;
import org.mabb.fontverter.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.CoreMatchers.instanceOf;

public class TestWoffParser {
    @Test
    public void parseWoff1FontFile_thenParsedFontIsWoff1() throws Exception {
        WoffFont font = parseWoff1Font(TestUtils.TEST_PATH + "Open-Sans-WOFF-1.0.woff");

        Assert.assertEquals(WoffHeader.WOFF_1_SIGNATURE, font.header.signature);
        Assert.assertThat(font, instanceOf(Woff1Font.class));
    }

    @Test
    public void woff1Font_thenCorrectNumberOfTablesAreRead() throws IOException {
        WoffFont font = parseWoff1Font(TestUtils.TEST_PATH + "Open-Sans-WOFF-1.0.woff");
        Assert.assertEquals(18, font.tables.size());
    }

    @Test
    public void parseWoff1_thenAllTablesHaveData() throws IOException {
        WoffFont font = parseWoff1Font(TestUtils.TEST_PATH + "Open-Sans-WOFF-1.0.woff");

        for (WoffTable tableOn : font.getTables())
            Assert.assertThat(tableOn.getCompressedData().length, greaterThan(3));
    }

    @Test
    public void parseWoff2FontFile_thenParsedFontIsWoff2() throws Exception {
        WoffFont font = parseWoff2Font(TestUtils.TEST_PATH + "Open-Sans-WOFF-2.0.woff2");

        Assert.assertEquals(WoffHeader.WOFF_2_SIGNATURE, font.header.signature);
        Assert.assertThat(font, instanceOf(Woff2Font.class));
    }

    @Test
    public void parseWoff2FontFile_thenAllTablesAreRead() throws IOException {
        WoffFont font = parseWoff2Font(TestUtils.TEST_PATH + "Open-Sans-WOFF-2.0.woff2");

        Assert.assertEquals(17, font.tables.size());
    }

    @Test
    public void parseWoff2_thenAllTablesHaveData() throws IOException {
//        WoffFont font = parseWoff2Font(TestUtils.TEST_PATH + "Open-Sans-WOFF-2.0.woff2");
//
//        for (WoffTable tableOn : font.getTables())
//            Assert.assertThat(tableOn.getTag(), tableOn.tableData.length, greaterThan(0));
    }


    private WoffFont parseWoff1Font(String file) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File(file));
        WoffParser parser = new WoffParser();
        return parser.parse(data);
    }

    private Woff2Font parseWoff2Font(String file) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File(file));
        Woff2Parser parser = new Woff2Parser();
        return (Woff2Font) parser.parse(data);
    }
}
