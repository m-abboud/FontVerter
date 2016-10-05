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
