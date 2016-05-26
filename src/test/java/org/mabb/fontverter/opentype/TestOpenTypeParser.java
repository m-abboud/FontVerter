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
    public void parse_OTF_table_directory_gives_sameNumberOfTables() throws IOException, IllegalAccessException, InstantiationException {
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
