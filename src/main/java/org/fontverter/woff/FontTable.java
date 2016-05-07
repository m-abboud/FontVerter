package org.fontverter.woff;

import com.google.common.io.ByteStreams;
import org.fontverter.FontVerterUtils;
import org.fontverter.io.ByteDataOutputStream;
import org.fontverter.woff.WoffConstants.TableFlagType;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliCompressor;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class FontTable {
    protected final byte[] tableData;
    protected final TableFlagType flag;
    protected byte[] compressedData;

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

    public static class WoffV1FontTable extends FontTable{
        private int offset;

        public WoffV1FontTable(byte[] table, TableFlagType flag) {
            super(table, flag);
        }

        public byte[] getCompressedTableData() throws IOException {
            if (compressedData == null)
                compressedData = zlibCompress(tableData);

            return compressedData;
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


        public byte[] getDirectoryData() throws IOException {
            WoffOutputStream writer = new WoffOutputStream();

            // tag would be here but spec says optional and it's same as flag
            writer.writeUIntBase128(tableData.length);
            writer.writeUnsignedInt(getCompressedTableData().length);

            writer.writeInt(compressedData.length);
            writer.write(tableData.length);

            int checksum = (int) FontVerterUtils.getTableChecksum(tableData);
            writer.writeInt(checksum);

            return writer.toByteArray();
        }

//        UInt32	tag	4-byte sfnt table identifier.
//        UInt32	offset	Offset to the data, from beginning of WOFF file.
//        UInt32	compLength	Length of the compressed data, excluding padding.
//        UInt32	origLength	Length of the uncompressed table, excluding padding.
//        UInt32	origChecksum	Checksum of the uncompressed table.

    } 
}
