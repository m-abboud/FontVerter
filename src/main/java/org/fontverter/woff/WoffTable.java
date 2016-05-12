package org.fontverter.woff;

import org.apache.commons.lang3.ArrayUtils;
import org.fontverter.FontVerterUtils;
import org.fontverter.woff.WoffConstants.TableFlagType;

import java.io.IOException;

public abstract class WoffTable {
    int transformLength;
    int originalLength;
    protected TableFlagType flag;
    protected byte[] tableData;
    protected byte[] compressedData;

    protected int paddingAdded = 0;

    public WoffTable(byte[] table, TableFlagType flag) {
        this.tableData = table;
        originalLength = table.length;
        this.flag = flag;
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

}
