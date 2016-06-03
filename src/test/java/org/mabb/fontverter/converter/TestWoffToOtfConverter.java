package org.mabb.fontverter.converter;

import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.SfntHeader;

import static org.mabb.fontverter.TestUtils.TEST_PATH;
import static org.mabb.fontverter.TestUtils.saveTempFile;

public class TestWoffToOtfConverter {

    @Test
    public void convertWoff1_toOtf_validatorPasses() throws Exception {
        FVFont otfFont = FontVerter.convertFont(TEST_PATH + "Open-Sans-WOFF-1.0.woff", FontVerter.FontFormat.OTF);
        byte[] fontData = otfFont.getData();
        saveTempFile(fontData, "Open-Sans-WOFF-1.0.otf");

        Assert.assertTrue(otfFont.doesPassStrictValidation());
    }

    @Test
    public void convertWoff2_toOtf_validatorPasses() throws Exception {
        FVFont otfFont = FontVerter.convertFont(TEST_PATH + "Open-Sans-WOFF-2.0.woff2", FontVerter.FontFormat.OTF);
        byte[] fontData = otfFont.getData();
        saveTempFile(fontData, "Open-Sans-WOFF-2.0.otf");

        Assert.assertTrue(otfFont.doesPassStrictValidation());
    }

    @Test
    public void convertWoff2_toOtf_thenOtfHasSameNumberOfTablesAsWoff() throws Exception {
        FVFont font = FontVerter.convertFont(TEST_PATH + "Open-Sans-WOFF-2.0.woff2", FontVerter.FontFormat.OTF);
        OpenTypeFont otfFont = (OpenTypeFont) font;

        Assert.assertEquals(17, otfFont.getTables().size());
    }

    @Test
    public void convertWoff2_toOtf_thenOtfSfntFlavor_calculatedCorrectly() throws Exception {
        FVFont font = FontVerter.convertFont(TEST_PATH + "Open-Sans-WOFF-2.0.woff2", FontVerter.FontFormat.OTF);
        OpenTypeFont otfFont = (OpenTypeFont) font;

        Assert.assertEquals(SfntHeader.VERSION_1, otfFont.getSfntHeader().sfntFlavor);
    }
}
