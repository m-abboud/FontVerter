package org.fontverter.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

public class ByteDataInputStream extends DataInputStream {
    public ByteDataInputStream(byte[] data) {
        super(new ByteArrayInputStream(data));
    }

}
