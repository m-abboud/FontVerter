package org.fontverter.woff;

import org.apache.commons.io.FileUtils;
import org.fontverter.FontAdapter;
import org.fontverter.FontVerter;
import org.fontverter.FontVerter.FontFormat;
import org.fontverter.FontVerterConfig;
import org.fontverter.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestOtfToWoffConverter {
    @Test
    public void convertCffToWoff_woffFontHasSameNumberOfTables() throws Exception {
        WoffFont woffFont = (WoffFont) FontVerter.convertFont(TestUtils.TEST_PATH + "test.cff", FontFormat.WOFF);
        Assert.assertEquals(9, woffFont.getTables().size());
    }

    @Test
    public void convertWoff() throws Exception {
        FontAdapter woffFont = FontVerter.convertFont(TestUtils.TEST_PATH + "test.cff", FontFormat.WOFF);
        saveTempFile(woffFont.getData(), "FontVerter+SimpleTestFont.woff");
        new File(TestUtils.tempOutputPath + "FontVerter+SimpleTestFont_validate.html").delete();
    }

    @Test
    public void convertWoff2() throws Exception {
        FontVerterConfig config = FontVerterConfig.globalConfig();
        config.setWoffVersion(2);
        FontVerterConfig.setGlobalConfig(config);

        String fontFile = TestUtils.TEST_PATH + "test.cff";
        FontAdapter woffFont = FontVerter.convertFont(fontFile, FontFormat.WOFF);
        saveTempFile(woffFont.getData(), "FontVerter+SimpleTestFont.woff");

    }

    private static void saveTempFile(byte[] data, String fileName) throws Exception {
        File outputFile = new File(TestUtils.tempOutputPath + fileName);
        if (outputFile.exists())
            outputFile.delete();

        FileUtils.writeByteArrayToFile(outputFile, data);
    }
}
