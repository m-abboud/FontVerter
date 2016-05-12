package org.fontverter.woff;

import org.apache.commons.io.FileUtils;
import org.fontverter.FontAdapter;
import org.fontverter.FontVerter;
import org.fontverter.FontVerter.FontFormat;
import org.fontverter.FontVerterConfig;
import org.fontverter.TestUtils;
import org.fontverter.woff.WoffConstants.TableFlagType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.fontverter.woff.WoffConstants.TableFlagType.*;

public class TestOtfToWoffConverter {
    @Test
    public void convertCffToWoff_woffFontHasSameNumberOfTables() throws Exception {
        WoffFont woffFont = (WoffFont) FontVerter.convertFont(TestUtils.TEST_PATH + "test.cff", FontFormat.WOFF1);
        Assert.assertEquals(9, woffFont.getTables().size());
    }

    @Test
    public void convertCff_ToOtf_ToWoff1_validatorPasses() throws Exception {
        FontAdapter woffFont = FontVerter.convertFont(TestUtils.TEST_PATH + "test.cff", FontFormat.WOFF1);
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
        WoffFont parsedFont = convertAndReparseWoff2("test.cff");

        Woff2Validator validator = new Woff2Validator();
        validator.validateWithExceptionsThrown((Woff2Font) parsedFont);
    }

    @Test
    public void convertOtfToWoff2_thenAllOtfTableTypes_arePresentInWoff2Font() throws Exception {
        WoffFont parsedFont = convertAndReparseWoff2("test.cff");

        TableFlagType[] flags = new TableFlagType[]{CFF, post, OS2, head, hmtx, cmap, name, hhea, maxp};
        for (TableFlagType flagOn : flags) {
            boolean flagFound = false;
            for (WoffTable table : parsedFont.getTables()) {
                if(flagOn == table.flag)
                    flagFound = true;
            }

            Assert.assertTrue(String.format("flag %s type not found", flagOn), flagFound);
        }
    }

    @Test
    public void convertCffToWoff2_sfntSizeSameAsOtfFontSize() throws Exception {
        WoffFont parsedFont = (WoffFont) FontVerter.convertFont(TestUtils.TEST_PATH + "test.cff", FontFormat.WOFF2);
        int otfLength = parsedFont.getFonts().get(0).getData().length;

        WoffParser parser = new WoffParser();
        WoffFont reparsedFont = parser.parse(parsedFont.getData());

        Assert.assertEquals(otfLength, reparsedFont.header.totalSfntSize);
    }

    private WoffFont convertAndReparseWoff2(String cffFile) throws Exception {
        FontAdapter woffFont = FontVerter.convertFont(TestUtils.TEST_PATH + cffFile, FontFormat.WOFF2);
        byte[] fontData = woffFont.getData();
        saveTempFile(fontData, "FontVerter+SimpleTestFont.woff2");

        File outputFile = new File("C:\\projects\\Pdf2Dom - type1c-fonts\\FontVerter+SimpleTestFont.woff2");
        if (outputFile.exists())
            outputFile.delete();

        FileUtils.writeByteArrayToFile(outputFile, fontData);

        // parse bytes and rebuild font obj for validation so know data is written right
        WoffParser parser = new WoffParser();
        return parser.parse(fontData);
    }


    private static void saveTempFile(byte[] data, String fileName) throws Exception {
        File outputFile = new File(TestUtils.tempOutputPath + fileName);
        if (outputFile.exists())
            outputFile.delete();

        FileUtils.writeByteArrayToFile(outputFile, data);
    }
}
