package org.fontverter.opentype;

import org.fontverter.FontAdapter;
import org.fontverter.FontConverter;
import org.fontverter.FontVerter;
import org.fontverter.FontVerterUtils;

public class OtfFontAdapter implements FontAdapter {
    private OpenTypeFont font;

    public OtfFontAdapter(OpenTypeFont font) {
        this.font = font;
    }

    public OtfFontAdapter() {
    }

    public byte[] getData() {
        return new byte[0];
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "OTTO");
    }

    public void read(byte[] fontFile) {
        font = OpenTypeFont.createBlankFont();
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) {
        return null;
    }

    public OpenTypeFont getFont() {
        return font;
    }
}
