package org.fontverter.opentype;

import org.fontverter.*;

import java.io.IOException;

import static org.fontverter.FontVerter.*;
import static org.fontverter.opentype.OtfNameConstants.*;

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
        return FontVerterUtils.bytesStartsWith(fontFile, "OTTO");
    }

    public void read(byte[] fontFile) throws IOException{
        try {
            font = new OpenTypeParser().parse(fontFile);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public FontConverter createConverterForType(FontFormat fontFormat) throws FontNotSupportedException {
        if (fontFormat == FontFormat.WOFF1)
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
}
