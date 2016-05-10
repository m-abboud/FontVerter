package org.fontverter.woff;

import org.apache.commons.lang3.ArrayUtils;
import org.fontverter.FontVerterUtils;
import org.fontverter.woff.WoffConstants.TableFlagType;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.IOException;

public class FontTable {
    protected final byte[] tableData;
    protected final TableFlagType flag;
    protected byte[] cachedCompressedData;
    protected int paddingAdded = 0;

    public FontTable(byte[] table, TableFlagType flag) {
        this.tableData = table;
        this.flag = flag;
    }

    public byte[] getDirectoryData() throws IOException {
        WoffOutputStream writer = new WoffOutputStream();
        writer.writeUnsignedInt8(flag.getValue());
        // tag would be here but spec says optional and it's same as flag
        writer.writeUIntBase128(tableData.length);
        writer.writeUIntBase128(getCompressedTableData().length - paddingAdded);

        return writer.toByteArray();
    }

    public byte[] getCompressedTableData() throws IOException {
        if (cachedCompressedData == null)
            cachedCompressedData = brotliCompress(tableData);

        return padTableData(cachedCompressedData);
    }

    private static byte[] brotliCompress(byte[] bytes) throws IOException {
        BrotliLibraryLoader.loadBrotli();

        BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);
        return streamCompressor.compressArray(bytes, true);
    }

    public int origLength() {
        return tableData.length;
    }

    protected byte[] padTableData(byte[] tableData) {
        byte[] padding = FontVerterUtils.tablePaddingNeeded(tableData);
        if (padding.length != 0)
            paddingAdded = padding.length;
        return ArrayUtils.addAll(tableData, padding);
    }
}
