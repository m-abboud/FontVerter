package org.fontverter.woff;

import org.fontverter.io.ByteSerializerException;
import org.fontverter.opentype.OpenTypeFont;

import java.io.IOException;

public class OtfToWoffConverter {
    OpenTypeFont otfFont;
    private WoffFont woffFont;

    public OtfToWoffConverter(OpenTypeFont cffFont) {
        this.otfFont = cffFont;
    }

    public WoffFont generateFont() throws IOException {
        woffFont = WoffFont.createBlankFont();

        return woffFont;
    }
}
