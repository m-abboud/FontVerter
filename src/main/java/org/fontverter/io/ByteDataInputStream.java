package org.fontverter.io;

import java.io.*;

public class ByteDataInputStream extends DataInputStream {
    public ByteDataInputStream(byte[] data) {
        super(new ByteArrayInputStream(data));
    }

    public long readUnsignedInt() throws IOException
    {
        long byte1 = read();
        long byte2 = read();
        long byte3 = read();
        long byte4 = read();
        if (byte4 < 0)
        {
            throw new EOFException();
        }
        return (byte1 << 24) + (byte2 << 16) + (byte3 << 8) + (byte4 << 0);
    }
}
