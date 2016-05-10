import org.apache.commons.io.FileUtils;
import org.fontverter.FontAdapter;
import org.fontverter.FontVerter;
import org.fontverter.FontVerter.FontFormat;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestOtfToWoffConverter {

    @Test
    public void convertWoff() throws Exception {
        FontAdapter woffFont = FontVerter.convertFont(TestUtils.testPath + "test.cff", FontFormat.WOFF);
        saveTempFile(woffFont.getData(), "FontVerter+SimpleTestFont.woff");
    }

    private static void saveTempFile(byte[] data, String fileName) throws Exception {
        File outputFile = new File(TestUtils.tempOutputPath + fileName);
        if (outputFile.exists())
            outputFile.delete();

        FileUtils.writeByteArrayToFile(outputFile, data);
    }
}
