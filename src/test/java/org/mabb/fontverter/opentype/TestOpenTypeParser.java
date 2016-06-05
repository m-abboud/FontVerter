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

package org.mabb.fontverter.opentype;

import org.apache.commons.io.FileUtils;
import org.mabb.fontverter.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestOpenTypeParser {
    @Test
    public void given_OTF_CFF_FLAVOR_font_parseSfntHeader_thenParsedFontHasCorrectFlavor_and_NumOfTables() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeParser parser = new OpenTypeParser();
        byte[] fontData = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf"));
        OpenTypeFont font = parser.parse(fontData);

        Assert.assertEquals("OTTO", font.sfntHeader.sfntFlavor);
    }

    @Test
    public void parse_OTF_table_directory_gives_sameNumberOfTables()
            throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeParser parser = new OpenTypeParser();
        byte[] fontData = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf"));
        OpenTypeFont font = parser.parse(fontData);

        Assert.assertEquals(9, font.sfntHeader.numTables);
    }

    @Test
    public void parse_TTF() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeParser parser = new OpenTypeParser();
        byte[] fontData = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "KJJTAM+TrebuchetMS.ttf"));
        OpenTypeFont font = parser.parse(fontData);

        Assert.assertEquals(13, font.sfntHeader.numTables);
    }
}
