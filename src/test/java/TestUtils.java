import org.apache.fontbox.ttf.OTFParser;
import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.validator.OpenTypeFontValidator;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TestUtils {
    static final String testPath = "src/test/files/";

    public static void runAllValidators(OpenTypeFont font) throws Exception {
        runInternalValidator(font);
        try {
            fontboxValidate(font.getSourceFile());
        } catch (IOException ex) {
            // fontbox bug location table is not mandatory for otf with cff type fonts
            // putting patch in soon
            if(!ex.getMessage().contains("loca is mandatory"))
                throw ex;
        }
        jdkFontValidate(font.getSourceFile());
    }

    public static  void runInternalValidator(OpenTypeFont font) throws Exception{
        OpenTypeFontValidator validator = new OpenTypeFontValidator();
        validator.validateWithExceptionsThrown(font);
    }

    public static void jdkFontValidate(File file) throws FontFormatException, IOException {
        Font.createFont(Font.TRUETYPE_FONT, file);
    }

    public static  void fontboxValidate(File file) throws IOException {
        // fontbox for validating generated fonts, fontbox has good pdf type font parsing no generation tho
        // but font classes have package local constructors
        OTFParser parser = new OTFParser();
        org.apache.fontbox.ttf.OpenTypeFont font = parser.parse(file);
        font.getName();
    }
}
