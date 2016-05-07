package org.fontverter.opentype;

import org.fontverter.*;

import java.io.IOException;

import static org.fontverter.FontVerter.*;

public class OtfFontAdapter implements FontAdapter {
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

    public void read(byte[] fontFile) {
        font = OpenTypeFont.createBlankFont();
    }

    public FontConverter createConverterForType(FontFormat fontFormat) throws FontNotSupportedException {
        if (fontFormat == FontFormat.WOFF)
            return new OtfToWoffConverter();

        throw new FontNotSupportedException("Font conversion not supported");
    }

    public OpenTypeFont getFont() {
        return font;
    }
}
