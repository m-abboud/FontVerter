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

package org.mabb.fontverter.opentype;

import org.apache.commons.lang3.ArrayUtils;
import org.mabb.fontverter.io.DataTypeProperty;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HorizontalHeadTable extends OpenTypeTable {
    @DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)
    public float version;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short ascender;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short descender;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short lineGap;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    public int advanceWidthMax;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short minLeftSideBearing;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short minRightSideBearing;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short xMaxExtent;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short caretSlopeRise;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short caretSlopeRun;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short caretOffset;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short reserved1;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short reserved2;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short reserved3;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short reserved4;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    public short metricDataFormat;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    public int numberOfHMetrics;

    @Override
    public String getTableType() {
        return "hhea";
    }

    public static HorizontalHeadTable createDefaultTable() {
        HorizontalHeadTable table = new HorizontalHeadTable();
        table.version = 1;
        table.ascender = 796;
        table.descender = -133;
        table.lineGap = 90;
        table.advanceWidthMax = 1430;
        table.minLeftSideBearing = 0;
        table.minRightSideBearing = 0;
        table.xMaxExtent = 1193;
        table.caretSlopeRise = 1;
        table.caretSlopeRun = 0;
        table.caretOffset = 0;

        table.reserved1 = 0;
        table.reserved2 = 0;
        table.reserved3 = 0;
        table.reserved4 = 0;
        table.metricDataFormat = 0;
        table.numberOfHMetrics = 0;
        return table;
    }

    void normalize() throws IOException {
        if (font.getHmtx().isFromParsedFont)
            return;

        font.getHmtx().normalize();
        int[] widths = font.getHmtx().getAdvanceWidths();
        this.numberOfHMetrics = widths.length;

        List<Integer> widthsList = Arrays.asList(ArrayUtils.toObject(widths));
        if (widthsList.size() > 0)
            advanceWidthMax = Collections.max(widthsList);


    }
}
