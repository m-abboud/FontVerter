package org.fontverter.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Adds special font data type write functionality to data output stream
 * todo split out into woff/otf specific maybe
 */
public class FontDataOutputStream extends DataOutputStream {
    public static final Charset OPEN_TYPE_CHARSET = Charset.forName("ISO-8859-1");

    public FontDataOutputStream(Charset encoding) {
        super(new ByteArrayOutputStream());
    }

    public byte[] toByteArray() {
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    public void writeString(String string) throws IOException {
        byte[] bytes = string.getBytes(OPEN_TYPE_CHARSET);
        out.write(bytes);
    }

    public void writeUnsignedShort(int num) throws IOException {
        writeShort(num);
    }

    public void writeUnsignedInt(int num) throws IOException {
        writeInt(num);
    }

    public void writeUnsignedInt8(int num) throws IOException {
        byte int8 = (byte) (num >>> 24);
        writeByte(int8);
    }

    public void write32Fixed(float num) throws IOException {
        // DataOutputStream.writeFloat won't do it right for 16x16 float at least for OTF
        writeShort((int) num);
        float decimalOnlyVal = (num - (int) num);
        int decimalVal = (int) (decimalOnlyVal * 65536);
        writeUnsignedShort(decimalVal);
    }
}
