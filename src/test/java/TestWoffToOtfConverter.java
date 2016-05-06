import org.apache.commons.io.FileUtils;
import org.fontverter.FontVerter;
import org.fontverter.woff.OtfToWoffConverter;
import org.fontverter.woff.WoffFont;
import org.fontverter.woff.WoffInputStream;
import org.fontverter.woff.WoffOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestWoffToOtfConverter {

    @Test
    public void convertWoff() throws Exception {
        WoffFont font = convertAndSaveFile("FontVerter+SimpleTestFont");
    }



    public static WoffFont convertAndSaveFile(String fileName) throws Exception {
        OtfToWoffConverter converter = new OtfToWoffConverter();
        String filePath = TestUtils.testPath + fileName + ".otf";
        WoffFont generatedFont = (WoffFont) converter.convertFont(FontVerter.readFont(filePath));

        File outputFile = new File(TestUtils.tempOutputPath + fileName + ".woff");
        if(outputFile.exists())
            outputFile.delete();

        byte[] fontData = generatedFont.getData();
        FileUtils.writeByteArrayToFile(outputFile, fontData);

        return generatedFont;
    }
}
