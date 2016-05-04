import org.apache.commons.io.FileUtils;
import org.apache.fontbox.ttf.OTFParser;
import org.fontverter.cff.CffToOpenTypeConverter;
import org.fontverter.opentype.FontSerializerException;
import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OpenTypeValidator;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.fontverter.opentype.OtfNameConstants.RecordType.*;

public class TestCffToOtfConverter {
    private static final String testPath = "src/test/files/";
    private static final String tempOutputPath = "src/test/test-output/";

    @Test
    public void convertSimpleFont_fontValidatorsPass() throws Exception {
        OpenTypeFont font = convertAndSaveFile("FontVerter+SimpleTestFont");
        runAllValidators(font);
    }

    @Test
    public void convertFullAlphabetFont_fontValidatorsPass() throws Exception {
        OpenTypeFont font = convertAndSaveFile("FontVerter+FullAlphabetFont");
        // windows font viewer failing atm
        runAllValidators(font);
    }

    @Test
    public void convert_CFF_fullAlphabetFont_then_OTF_has94Glyphs() throws Exception {
        OpenTypeFont font = convert("FontVerter+FullAlphabetFont");

        // getNumGlyphs includes padded glyph so -1 for padding,there's 4 actual glyphs
        Assert.assertEquals(94, font.cmap.getGlyphCount() - 1);
    }

    @Test
    public void convert_CFF_withDictNameEntries_Then_OTF_hasFontNameRecordsSet() throws Exception {
        OpenTypeFont font = convert("FontVerter+SimpleTestFont");

        Assert.assertEquals("SimpleTestFont", font.name.getName(FONT_FAMILY));
        Assert.assertEquals("FontVerter+SimpleTestFont", font.name.getName(FULL_FONT_NAME));
        Assert.assertEquals("Medium", font.name.getName(FONT_SUB_FAMILY));
    }

    @Test
    public void convertCFF_OutputHasDescenderValue() throws Exception {
        OpenTypeFont font = convert("FontVerter+SimpleTestFont");

        Assert.assertEquals(-133, font.hhea.descender);
    }

    @Test
    public void convertCFF_OutputHasBoundingBox() throws Exception {
        OpenTypeFont font = convert("FontVerter+SimpleTestFont");

        Assert.assertEquals(26, font.head.getxMin());
        Assert.assertEquals(-2, font.head.getyMin());
        Assert.assertEquals(1297, font.head.getxMax());
        Assert.assertEquals(793, font.head.getyMax());
    }

    private OpenTypeFont convertAndSaveFile(String fileName) throws Exception {
        File outputFile = new File(tempOutputPath + fileName + ".otf");
        if(outputFile.exists())
            outputFile.delete();

        OpenTypeFont generatedFont = convert(fileName);
        byte[] fontData = generatedFont.getFontData();
        FileUtils.writeByteArrayToFile(outputFile, fontData);
        generatedFont.setSourceFile(outputFile);

        return generatedFont;
    }

    private OpenTypeFont convert(String fileName) throws IOException, FontSerializerException {
        byte[] cff = FileUtils.readFileToByteArray(new File(testPath + fileName + ".cff"));
        CffToOpenTypeConverter gen = new CffToOpenTypeConverter(cff);
        return gen.generateFont();
    }

    private void runInternalValidator(OpenTypeFont font) throws Exception{
        OpenTypeValidator validator = new OpenTypeValidator();
        validator.validateWithExceptionsThrown(font);
    }

    private void jdkFontValidate(File file) throws FontFormatException, IOException {
        Font.createFont(Font.TRUETYPE_FONT, file);
    }

    private void fontboxValidate(File file) throws IOException {
        // fontbox for validating generated fonts, fontbox has good pdf type font parsing no generation tho
        // but font classes have package local constructors
        OTFParser parser = new OTFParser();
        org.apache.fontbox.ttf.OpenTypeFont font = parser.parse(file);
        font.getName();
    }

    private void runAllValidators(OpenTypeFont font) throws Exception {
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
}
