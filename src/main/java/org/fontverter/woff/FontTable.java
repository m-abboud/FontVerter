package org.fontverter.woff;

import com.google.common.io.ByteStreams;
import org.fontverter.io.ByteDataOutputStream;
import org.fontverter.woff.WoffConstants.TableFlagType;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliCompressor;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class FontTable {
    private final byte[] tableData;
    private final TableFlagType flag;
    private byte[] compressedData;

    public FontTable(byte[] table, TableFlagType flag) {
        this.tableData = table;
        this.flag = flag;
    }

    public byte[] getDirectoryData() throws IOException {
        WoffOutputStream writer = new WoffOutputStream();
        writer.writeUnsignedInt8(flag.getValue());
        // tag would be here but spec says optional and it's same as flag
        writer.writeUIntBase128(tableData.length);
        writer.writeUIntBase128(getCompressedTableData().length);

        return writer.toByteArray();
    }

    public byte[] getCompressedTableData() throws IOException {
        if (compressedData == null)
            compressedData = brotliCompress(tableData);

        return compressedData;
    }


    private static byte[] brotliCompress(byte[] bytes) throws IOException {
        BrotliLibraryLoader.loadBrotli();

        BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
        return streamCompressor.compressArray(bytes, true);
    }

    private static byte[] zlibCompress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream deflater = new DeflaterOutputStream(out);
        deflater.write(bytes);
        out.close();
        try {
            ByteStreams.copy(new ByteArrayInputStream(bytes), deflater);
        } finally {
            deflater.close();
        }
        return out.toByteArray();
    }
}
