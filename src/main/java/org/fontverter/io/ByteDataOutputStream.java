package org.fontverter.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class ByteDataOutputStream extends DataOutputStream {
    public static final Charset openTypeCharset = Charset.forName("ISO-8859-1");

    private final Charset encoding;

    public ByteDataOutputStream(Charset encoding)
    {
        super(new ByteArrayOutputStream());
        this.encoding = encoding;
    }

    public byte[] toByteArray()
    {
        return ((ByteArrayOutputStream)out).toByteArray();
    }

    public void writeString(String string) throws IOException
    {
        out.write(string.getBytes(openTypeCharset));
    }

    public void writeUnsignedShort(int num) throws IOException
    {
        writeShort(num);
    }

    public void writeUnsignedInt(int num) throws IOException
    {
        writeInt(num);
    }

    public void write32Fixed(float num) throws IOException
    {
        writeShort((int) num);
        float decimalOnlyVal = (num - (int) num);
        int decimalVal = (int) (decimalOnlyVal * 65536);
        writeUnsignedShort(decimalVal);
    }
}
