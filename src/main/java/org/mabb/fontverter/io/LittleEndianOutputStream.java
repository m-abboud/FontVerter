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
import org.mabb.fontverter.NotImplementedException;

import java.io.IOException;

public class LittleEndianOutputStream implements FontDataOutput {
    FontDataOutputStream stream = new FontDataOutputStream();

    public byte[] toByteArray() {
        return stream.toByteArray();
    }

    public void writeString(String string) throws IOException {
        throw new NotImplementedException();
    }

    public void writeUnsignedShort(int num) throws IOException {
        EndianUtils.writeSwappedShort(stream, (short) num);
    }

    public void writeUnsignedInt(int num) throws IOException {
        EndianUtils.writeSwappedInteger(stream, num);
    }

    public void writeUnsignedInt8(int num) throws IOException {
        throw new NotImplementedException();
    }

    public void write32Fixed(float num) throws IOException {
        throw new NotImplementedException();
    }

    public int currentPosition() {
        return stream.currentPosition();
    }

    public void close() throws IOException {
        stream.close();
    }

    public void write(int b) throws IOException {
        stream.write(b);
    }

    public void write(byte[] b) throws IOException {
        stream.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        stream.write(b);
    }

    public void writeBoolean(boolean v) throws IOException {
        stream.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        stream.write(v);
    }

    public void writeShort(int v) throws IOException {
        EndianUtils.writeSwappedShort(stream, (short) v);
    }

    public void writeChar(int v) throws IOException {
        stream.writeChar(v);
    }

    public void writeInt(int v) throws IOException {
        EndianUtils.writeSwappedInteger(stream, v);
    }

    public void writeLong(long v) throws IOException {
        EndianUtils.writeSwappedLong(stream, v);
    }

    public void writeFloat(float v) throws IOException {
        EndianUtils.writeSwappedFloat(stream, v);
    }

    public void writeDouble(double v) throws IOException {
        EndianUtils.writeSwappedDouble(stream, v);

    }

    public void writeBytes(String s) throws IOException {
        stream.writeBytes(s);
    }

    public void writeChars(String s) throws IOException {
        throw new NotImplementedException();
    }

    public void writeUTF(String s) throws IOException {
        throw new NotImplementedException();

    }

    public void flush() throws IOException {
        stream.flush();
    }
}
