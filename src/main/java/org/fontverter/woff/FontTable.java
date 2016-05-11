package org.fontverter.woff;

import org.apache.commons.lang3.ArrayUtils;
import org.fontverter.FontVerterUtils;
import org.fontverter.woff.WoffConstants.TableFlagType;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

public abstract class FontTable {
    protected final TableFlagType flag;
    protected final byte[] tableData;
    protected byte[] cachedCompressedData;

    protected int paddingAdded = 0;

    public FontTable(byte[] table, TableFlagType flag) {
        this.tableData = table;
        this.flag = flag;
    }

    protected abstract byte[] compress(byte[] data) throws IOException;

    protected abstract byte[] getDirectoryData() throws IOException;

    public byte[] getCompressedTableData() throws IOException {
        if (cachedCompressedData == null) {
            cachedCompressedData = compress(tableData);
            if (origLength() < cachedCompressedData.length)
                cachedCompressedData = tableData;
        }

        return padTableData(cachedCompressedData);
    }

    protected byte[] padTableData(byte[] tableData) {
        byte[] padding = FontVerterUtils.tablePaddingNeeded(tableData);
        if (padding.length != 0)
            paddingAdded = padding.length;
        return ArrayUtils.addAll(tableData, padding);
    }

    public int origLength() {
        return tableData.length;
    }

}
