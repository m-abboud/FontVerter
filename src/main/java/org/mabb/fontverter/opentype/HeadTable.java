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

import org.mabb.fontverter.FontVerterUtils;
import org.mabb.fontverter.io.DataTypeProperty;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class HeadTable extends OpenTypeTable {
    @DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)
    private float version;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)
    private float fontRevision;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.ULONG)
    private long checkSumAdjustment;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.ULONG)
    private long magicNumber;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    private int flags;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    private int unitsPerEm;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.LONG_DATE_TIME)
    private Calendar created;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.LONG_DATE_TIME)
    private Calendar modified;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short xMin;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short yMin;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short xMax;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short yMax;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    private int macStyle;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    private int lowestRecPPEM;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short fontDirectionHint;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short indexToLocFormat;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short glyphDataFormat;

    public String getTableType() {
        return "head";
    }

    public static HeadTable createDefaultTable() {
        HeadTable table = new HeadTable();
        table.version = 1;
        table.fontRevision = 1;
        table.checkSumAdjustment = 0;
        table.magicNumber = 0x5F0F3CF5;
        table.flags = 11;
        table.unitsPerEm = 1000;

        table.created = createDefaultDate();
        table.modified = createDefaultDate();

        table.xMin = 26;
        table.yMin = -2;
        table.xMax = 1200;
        table.yMax = 800;
        table.macStyle = 0;
        table.lowestRecPPEM = 6;
        table.fontDirectionHint = 2;
        table.indexToLocFormat = 1;
        table.glyphDataFormat = 0;
        return table;
    }

    private static Calendar createDefaultDate() {
        Calendar created = GregorianCalendar.getInstance();
        // author's birthday, a very important date
        created.set(1991, Calendar.MARCH, 21);
        return created;
    }

    public void checksumAdjustment(byte[] fontBytes) throws IOException {
        checkSumAdjustment = 0xB1B0AFBA - FontVerterUtils.getTableChecksum(fontBytes);
    }

    public short getyMin() {
        return yMin;
    }

    public void setMinY(short yMin) {
        this.yMin = yMin;
    }

    public short getxMin() {
        return xMin;
    }

    public void setMinX(short xMin) {
        this.xMin = xMin;
    }

    public short getxMax() {
        return xMax;
    }

    public void setMaxX(short xMax) {
        this.xMax = xMax;
    }

    public short getyMax() {
        return yMax;
    }

    public void setMaxY(short yMax) {
        this.yMax = yMax;
    }

    void resetCalculations() {
        checkSumAdjustment = 0;
        super.resetCalculations();
    }

    public boolean isLongIndexToLocFormat() {
        return indexToLocFormat == 1;
    }
}
