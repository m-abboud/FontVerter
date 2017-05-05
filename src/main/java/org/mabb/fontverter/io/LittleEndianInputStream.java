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

import org.apache.commons.io.EndianUtils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class LittleEndianInputStream extends FilterInputStream implements FontDataInput {
    Charset encoding = FontDataOutputStream.OPEN_TYPE_CHARSET;

    public LittleEndianInputStream(byte[] data) {
        super(new FontDataInputStream.SeekableByteArrayInputStream(data));
    }

    public long readUnsignedInt() throws IOException {
        return EndianUtils.readSwappedUnsignedInteger(this);
    }

    public String readString(int length) throws IOException {
        return null;
    }

    public byte[] readBytes(int length) throws IOException {
        return new byte[0];
    }

    public void seek(int offset) {

    }

    public int readUIntBase128() throws IOException {
        return 0;
    }

    public float readFixed32() throws IOException {
        return 0;
    }

    public int getPosition() {
        return 0;
    }

    public int[] readSplitBits(int numUpperBits) throws IOException {
        return new int[0];
    }

    public int[] readUnsignedShortArray(int length) throws IOException {
        return new int[0];
    }

    public void readFully(byte[] b) throws IOException {
    }

    public void readFully(byte[] b, int off, int len) throws IOException {

    }

    public int skipBytes(int n) throws IOException {
        return 0;
    }

    public boolean readBoolean() throws IOException {
        return false;
    }

    public byte readByte() throws IOException {
        return 0;
    }

    public int readUnsignedByte() throws IOException {
        return 0;
    }

    public short readShort() throws IOException {
        return EndianUtils.readSwappedShort(this);
    }

    public int readUnsignedShort() throws IOException {
        return EndianUtils.readSwappedUnsignedShort(this);
    }

    public char readChar() throws IOException {
        return 0;
    }

    public int readInt() throws IOException {
        return 0;
    }

    public long readLong() throws IOException {
        return 0;
    }

    public float readFloat() throws IOException {
        return 0;
    }

    public double readDouble() throws IOException {
        return 0;
    }

    public String readLine() throws IOException {
        return null;
    }

    public String readUTF() throws IOException {
        return null;
    }
}
