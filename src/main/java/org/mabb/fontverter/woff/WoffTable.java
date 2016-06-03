package org.mabb.fontverter.woff;

import org.apache.commons.lang3.ArrayUtils;
import org.mabb.fontverter.FontVerterUtils;
import org.mabb.fontverter.woff.WoffConstants.TableFlagType;

import java.io.IOException;

public abstract class WoffTable {
    int transformLength;
    int originalLength;
    protected byte[] tableData;
    protected byte[] compressedData;
    protected int paddingAdded = 0;

    public WoffTable(byte[] table) {
        this.tableData = table;
        originalLength = table.length;
    }

    protected abstract byte[] compress(byte[] data) throws IOException;

    protected abstract byte[] getDirectoryData() throws IOException;

    public byte[] getCompressedData() throws IOException {
        if (compressedData == null) {
            compressedData = compress(tableData);
            if (origLength() < compressedData.length)
                compressedData = tableData;
        }

        return compressedData;
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

    public byte[] getTableData() {
        return tableData;
    }

    public abstract String getTag();
}
