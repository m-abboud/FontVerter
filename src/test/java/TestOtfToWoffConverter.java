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
        FontAdapter woffFont = FontVerter.convertFont(TestUtils.testPath + "FontVerter+SimpleTestFont.cff", FontFormat.WOFF);
        saveTempFile(woffFont.getData(), "FontVerter+SimpleTestFont.woff");
        Runtime rt = Runtime.getRuntime();
        String[] command = new String[]{"python", "woff-validate",
                "\"C:\\projects\\FontVerter\\src\\test\\test-output\\FontVerter+SimpleTestFont.woff\""};

        new File("C:\\projects\\FontVerter\\src\\test\\test-output\\FontVerter+SimpleTestFont_validate.html").delete();
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()), 8 * 1024);

        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

// read the output from the command

        String s = null;
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null)
            System.out.println(s.replace("[", "").replace("]", ""));

        s = null;
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdError.readLine()) != null)
            System.out.println(s.replace("[", "").replace("]", ""));
    }

    private static void saveTempFile(byte[] data, String fileName) throws Exception {
        File outputFile = new File(TestUtils.tempOutputPath + fileName);
        if (outputFile.exists())
            outputFile.delete();

        FileUtils.writeByteArrayToFile(outputFile, data);
    }
}
