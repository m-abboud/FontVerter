package org.fontverter.opentype;

import java.io.IOException;

public class FontSerializerException extends IOException {

    public FontSerializerException(Exception e) {
        super(e);
    }

    public FontSerializerException(String e) {
        super(e);
    }
}
