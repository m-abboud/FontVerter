package org.fontverter.woff;

import com.google.common.io.ByteStreams;
import com.jcraft.jzlib.*;
import org.fontverter.FontVerterUtils;

import java.io.*;
import java.util.zip.DeflaterOutputStream;

import static org.fontverter.io.ByteDataOutputStream.openTypeCharset;

public class Woff1FontTable extends FontTable {
    private int offset;

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

//    private static byte[] zlibCompress(byte[] bytes) throws IOException {
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        ZOutputStream zOut = new ZOutputStream(out, JZlib.W_ZLIB);
//        ObjectOutputStream objOut = new ObjectOutputStream(zOut);
//        objOut.writeObject(bytes);
//        zOut.close();
//        return out.toByteArray();
//    }

    public byte[] getDirectoryData() throws IOException {
        WoffOutputStream writer = new WoffOutputStream();

        writer.writeString(flag.toString());
        writer.writeInt(offset);
        writer.writeInt(getCompressedTableData().length - paddingAdded);
        writer.writeInt(tableData.length);

        int checksum = (int) FontVerterUtils.getTableChecksum(tableData);
        writer.writeInt(checksum);

        return writer.toByteArray();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
