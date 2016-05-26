package org.mabb.fontverter.opentype;

import org.mabb.fontverter.*;
import org.mabb.fontverter.opentype.validator.OpenTypeStrictValidator;
import org.mabb.fontverter.validator.RuleValidator;
import org.mabb.fontverter.validator.RuleValidator.FontValidatorError;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
        String[] headerMagicNums = new String[]{"\u0000\u0001\u0000\u0000", "OTTO"};
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
        try {
            OpenTypeStrictValidator validator = new OpenTypeStrictValidator();
            List<FontValidatorError> errors = validator.validate(font);
            return  errors.size() == 0;
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void normalize() {
        if (font.getOs2() == null)
            font.setOs2(OS2WinMetricsTable.createDefaultTable());
    }
}
