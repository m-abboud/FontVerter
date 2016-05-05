package org.fontverter.io;

import java.io.IOException;

public class ByteSerializerException extends IOException {

    public ByteSerializerException(Exception e) {
        super(e);
    }

    public ByteSerializerException(String e) {
        super(e);
    }
}
