package org.fontverter.woff;

import org.fontverter.io.ByteDataInputStream;

public class WoffParser {
    WoffFont font;
    private ByteDataInputStream fontData;

    public WoffParser() {
    }

    public WoffFont parse(byte[] data) {
        fontData = new ByteDataInputStream(data);
        return font;
    }
}
