package org.mabb.fontverter.opentype;

import org.mabb.fontverter.*;
import org.mabb.fontverter.opentype.validator.OpenTypeStrictValidator;
import org.mabb.fontverter.validator.RuleValidator;
import org.mabb.fontverter.validator.RuleValidator.FontValidatorError;
import org.mabb.fontverter.validator.RuleValidator.ValidatorErrorType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.mabb.fontverter.opentype.OpenTypeFont.SfntHeader.*;
import static org.mabb.fontverter.opentype.OtfNameConstants.*;

/* todo merge with OpenTypeFont class */
public class OtfFontAdapter implements FVFont {
    private OpenTypeFont font;

    public OtfFontAdapter(OpenTypeFont font) {
        this.font = font;
    }

    public OtfFontAdapter() {
    }

    public byte[] getData() throws IOException {
        return font.getFontData();
    }

    public boolean detectFormat(byte[] fontFile) {
        String[] headerMagicNums = new String[]{CFF_FLAVOR, VERSION_1, VERSION_2, VERSION_2_5};
        for (String magicNumOn : headerMagicNums)
            if (FontVerterUtils.bytesStartsWith(fontFile, magicNumOn))
                return true;

        return false;
    }

    public void read(byte[] fontFile) throws IOException {
        try {
            font = new OpenTypeParser().parse(fontFile);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        if (fontFormat == FontVerter.FontFormat.WOFF1)
            return new OtfToWoffConverter();

        throw new FontNotSupportedException("Font conversion not supported");
    }

    public OpenTypeFont getUnderlyingFont() {
        return font;
    }

    @Override
    public String getFontName() {
        return font.getNameTable().getName(RecordType.FULL_FONT_NAME);
    }

    public boolean doesPassStrictValidation() {
        return getStrictValidationErrors().size() == 0;
    }

    public List<FontValidatorError> getStrictValidationErrors() {
        try {
            OpenTypeStrictValidator validator = new OpenTypeStrictValidator();
            return validator.validate(font);
        } catch(Exception ex) {
            ex.printStackTrace();
            FontValidatorError error = new FontValidatorError(ValidatorErrorType.ERROR,
                    String.format("Exception running validator: %s %s", ex.getMessage(), ex.getClass()));

            ArrayList<FontValidatorError> errors = new ArrayList<FontValidatorError>();
            errors.add(error);

            return errors;
        }
    }

    public void normalize() {
        if (font.getOs2() == null)
            font.setOs2(OS2WinMetricsTable.createDefaultTable());

        if (font.getNameTable() == null)
            font.setName(NameTable.createDefaultTable());

        if (font.getPost() == null)
            font.setPost(PostScriptTable.createDefaultTable(font.getOpenTypeVersion()));
    }
}
