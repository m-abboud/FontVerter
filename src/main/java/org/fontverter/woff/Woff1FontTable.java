package org.fontverter.woff;

import com.google.common.io.ByteStreams;
import org.fontverter.FontVerterUtils;
import org.fontverter.io.ByteDataOutputStream;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static org.fontverter.io.ByteDataOutputStream.openTypeCharset;

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

        return padTableData(tableData);
//        return padTableData(cachedCompressedData);
    }

    private static byte[] zlibCompress1(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream deflater = new DeflaterOutputStream(out);
//        deflater.write(bytes);
//        deflater.flush();
        try {
            ByteStreams.copy(new ByteArrayInputStream(bytes), deflater);
        } finally {
            deflater.flush();
            deflater.close();
        }
        out.flush();
        return out.toByteArray();
    }

    private static byte[] zlibCompress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Deflater d = new Deflater();
        DeflaterOutputStream dout = new DeflaterOutputStream(out, d);
        dout.write(bytes);
        dout.finish();
        dout.close();

        return out.toByteArray();
    }



    public byte[] getDirectoryData() throws IOException {
        WoffOutputStream writer = new WoffOutputStream();

        writer.writeString(flag.toString());
        writer.writeInt(offset);
        writer.writeInt(getCompressedTableData().length - paddingAdded);
        writer.writeInt(tableData.length);


//        int checksum = (int) FontVerterUtils.getTableChecksum(tableData);
//        int checksum = (int) FontVerterUtils.getTableChecksum(padTableData(tableData));
        writer.writeUnsignedInt((int) checksum);

        return writer.toByteArray();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
