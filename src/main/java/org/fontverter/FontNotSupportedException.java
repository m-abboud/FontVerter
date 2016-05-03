package org.fontverter;

import java.io.IOException;

public class FontNotSupportedException extends IOException {
    public FontNotSupportedException(String s) {
        super(s);
    }
}
