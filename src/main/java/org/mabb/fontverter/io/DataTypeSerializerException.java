package org.mabb.fontverter.io;

import java.io.IOException;

public class DataTypeSerializerException extends IOException {
    public DataTypeSerializerException(Exception e) {
        super(e);
    }

    public DataTypeSerializerException(String message, Exception e) {
        super(message, e);
    }

    public DataTypeSerializerException(String e) {
        super(e);
    }
}
