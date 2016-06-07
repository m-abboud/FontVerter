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
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestCFFToOtfConverter;
import org.mabb.fontverter.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.number.OrderingComparison.lessThan;

public class TestOpenTypeParser {
    @Test
    public void given_OTF_CFF_FLAVOR_font_parseSfntHeader_thenParsedFontHasCorrectFlavor_and_NumOfTables() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");

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

    @Test
    public void givenGeneratedOTF_parseOs2WinMetricsTable_thenVersionIsSameAsOriginal() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont originalFont = TestCFFToOtfConverter.convert("cff/FontVerter+SimpleTestFont");
        OpenTypeFont parsedFont = (OpenTypeFont) FontVerter.readFont(originalFont.getData());

        Assert.assertEquals(originalFont.getOs2().getVersion(), parsedFont.getOs2().getVersion());
    }

    @Test
    public void givenGeneratedOTF_parseOs2WinMetricsTable_thenTypoAscenderIsSameAsOriginal() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont originalFont = TestCFFToOtfConverter.convert("cff/FontVerter+SimpleTestFont");
        OpenTypeFont parsedFont = (OpenTypeFont) FontVerter.readFont(originalFont.getData());

        Assert.assertEquals(originalFont.getOs2().getTypoAscender(), parsedFont.getOs2().getTypoAscender());
    }

    @Test
    public void given_OTF_file_whenParsed_thenCmapTableHasSameNumberOfGlyphMappings() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");

        Assert.assertEquals(4, font.getCmap().getGlyphMappings().size());
    }

    @Test
    public void given_OTF_file_whenParsed_thenMaximumProfileTable_hasSameNumberOfGlyphs() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");

        // 4 real glyphs, 1 padding is always added
        Assert.assertEquals(5, font.getMxap().getNumGlyphs());
    }

    @Test
    public void given_OTF_file_whenParsed_thenHorizontalHeadTables_AscenderTheSame() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");
        Assert.assertEquals(796, font.getHhea().ascender);
    }

    @Test
    public void given_OTF_file_whenParsed_thenHorizontalHeadTables_NumHMetricsTheSame() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");
        Assert.assertEquals(5, font.getHhea().numberOfHMetrics);
    }

    @Test
    public void given_OTF_file_whenParsed_thenHorizontalHeadTable_read_before_dependant_HmtxTable() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");

        int hheaIndex = font.getTables().indexOf(font.getHhea());
        int hmtxIndex = font.getTables().indexOf(font.getHmtx());

        Assert.assertThat(hheaIndex, lessThan(hmtxIndex));
    }

    @Test
    public void given_OTF_file_whenParsed_thenHmtxAdvanceWidthsSameLength() throws IOException, IllegalAccessException, InstantiationException {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "FontVerter+SimpleTestFont.otf");

        Assert.assertEquals(5, font.getHmtx().getAdvanceWidths().length);
    }

}
