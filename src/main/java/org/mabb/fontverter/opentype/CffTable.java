/*
 * Copyright (C) Matthew Abboud 2016
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

package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.DataTypeSerializerException;

public class CffTable extends OpenTypeTable {
    private byte[] data;

    public CffTable(byte[] data) {
        this.data = data;
    }

    public CffTable() {
    }

    protected byte[] generateUnpaddedData() {
        return data;
    }

    public String getTableType() {
        return "CFF ";
    }

    public void readData(byte[] data) throws DataTypeSerializerException {
        this.data = data;
    }
}
