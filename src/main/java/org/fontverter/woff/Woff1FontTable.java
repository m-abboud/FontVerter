package org.fontverter.woff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class Woff1FontTable extends FontTable {
    private int offset;
    long checksum;

    public Woff1FontTable(byte[] table, WoffConstants.TableFlagType flag) {
        super(table, flag);
    }

    public byte[] getCompressedTableData() throws IOException {
        if (cachedCompressedData == null) {
            cachedCompressedData = zlibCompress(tableData);
            if (origLength() < cachedCompressedData.length)
                cachedCompressedData = tableData;
        }

        return padTableData(cachedCompressedData);
    }

    private static byte[] zlibCompress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream compressStream = new DeflaterOutputStream(out);
        compressStream.write(bytes);
        compressStream.close();

        return out.toByteArray();
    }

    public byte[] getDirectoryData() throws IOException {
        WoffOutputStream writer = new WoffOutputStream();

        writer.writeString(flag.toString());
        writer.writeInt(offset);
        writer.writeInt(getCompressedTableData().length - paddingAdded);
        writer.writeInt(tableData.length);
        writer.writeUnsignedInt((int) checksum);

        return writer.toByteArray();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
