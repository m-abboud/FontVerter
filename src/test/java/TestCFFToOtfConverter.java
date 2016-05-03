import org.apache.commons.io.FileUtils;
import org.apache.fontbox.ttf.OTFParser;
import org.fontverter.cff.CffToOpenTypeConverter;
import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OpenTypeValidator;
import org.fontverter.opentype.OpenTypeValidator.FontValidatorError;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class TestCffToOtfConverter {
    private static final String testPath = "src/test/files/";

    @Test
    public void convert() throws Exception {
        byte[] cff = FileUtils.readFileToByteArray(new File(testPath + "test.cff"));

        CffToOpenTypeConverter gen = new CffToOpenTypeConverter(cff);
        OpenTypeFont generatedFont = gen.generateFont();
        byte[] fontData = generatedFont.getFontData();
        FileUtils.writeByteArrayToFile(new File("generate.otf"), fontData);
        FileUtils.writeByteArrayToFile(new File("C:\\Users\\Meese\\Documents\\generate.otf"), fontData);

        runInternalValidator(generatedFont);
        fontboxValidate("generate.otf");
        jdkFontValidate();
    }

    private void runInternalValidator(OpenTypeFont font) throws Exception {
        OpenTypeValidator validator = new OpenTypeValidator();
        List<FontValidatorError> errors = validator.validate(font);

        String validateMessage = "";
        for (FontValidatorError errorOn : errors)
            validateMessage += "\n" + errorOn.toString();

        if (errors.size() > 0)
            throw new Exception("Internal Validator error(s) " + validateMessage);
    }

    private void jdkFontValidate() throws FontFormatException, IOException {
        Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("generate.otf"));
    }

    private void fontboxValidate(String file) throws IOException {
        // fontbox for validating generated fonts, fontbox has good pdf type font parsing no generation tho
        // but font classes have package local constructors
        OTFParser parser = new OTFParser();
        org.apache.fontbox.ttf.OpenTypeFont font = parser.parse(file);
        font.getName();
    }
}
