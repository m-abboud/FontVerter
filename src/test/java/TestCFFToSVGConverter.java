import org.apache.commons.io.FileUtils;
import org.apache.fontbox.ttf.OTFParser;
import org.fontverter.cff.CFFToOpenTypeConverter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestCFFToSVGConverter
{
    private static final String testPath = "src/test/files/";

    @Test
    public void convert() throws Exception
    {
        byte[] cff = FileUtils.readFileToByteArray(new File(testPath + "test.cff"));

        CFFToOpenTypeConverter gen = new CFFToOpenTypeConverter(cff);
        FileUtils.writeByteArrayToFile(new File("generate.otf"), gen.generateOpenTypeFont());
        validate("generate.otf");
    }


    private void validate(String file) throws IOException
    {
        // pdfbox for validating generated fonts, fontbox has good font parsing no generation tho
        OTFParser parser = new OTFParser();
        parser.parse(file);
    }
}
