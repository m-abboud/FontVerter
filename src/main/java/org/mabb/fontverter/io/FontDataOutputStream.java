/*
 * Copyright (C) Maddie Abboud 2016
 *
 * FontVerter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FontVerter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FontVerter. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mabb.fontverter.io;

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
    private final Charset encoding;

    public FontDataOutputStream(Charset encoding) {
        super(new ByteArrayOutputStream());
        this.encoding = encoding;
    }

    public FontDataOutputStream() {
        super(new ByteArrayOutputStream());
        encoding = OPEN_TYPE_CHARSET;
    }

    public byte[] toByteArray() {
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    public void writeString(String string) throws IOException {
        byte[] bytes = string.getBytes(encoding);
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

    public int currentPosition() {
        return ((ByteArrayOutputStream) out).size();
    }
}
