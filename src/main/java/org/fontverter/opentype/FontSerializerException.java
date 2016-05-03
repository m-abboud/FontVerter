package org.fontverter.opentype;

public class FontSerializerException extends Exception {

    public FontSerializerException(Exception e) {
        super(e);
    }

    public FontSerializerException(String e) {
        super(e);
    }
}
