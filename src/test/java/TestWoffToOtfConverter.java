import org.apache.commons.io.FileUtils;
import org.fontverter.FontVerter;
import org.fontverter.woff.OtfToWoffConverter;
import org.fontverter.woff.WoffFont;
import org.fontverter.woff.WoffOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TestWoffToOtfConverter {

    @Test
    public void convertWoff() throws Exception {
        // getNumGlyphs includes padded glyph so -1 for padding,there's 4 actual glyphs
        WoffFont font = convertAndSaveFile("FontVerter+SimpleTestFont");
    }

    @Test
    public void woffOutputStreamUIntBase128() throws Exception {
        WoffOutputStream out = new WoffOutputStream();
        out.writeUIntBase128(555);
//        String base128code = Integer.toBinaryString(555);
//        String binary = Integer.toBinaryString(55);
        byte[] data = out.toByteArray();
        for(byte byteOn : data)
            System.out.println(byteOn);
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
