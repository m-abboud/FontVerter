package org.fontverter.opentype;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

class OtfWriter extends DataOutputStream
{
    public static final Charset openTypeCharEncoding = Charset.forName("ISO-8859-1");

    public OtfWriter()
    {
        super(new ByteArrayOutputStream());
    }

    public byte[] toByteArray()
    {
        return ((ByteArrayOutputStream)out).toByteArray();
    }

    public void writeString(String string) throws IOException
    {
        out.write(string.getBytes(openTypeCharEncoding));
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
