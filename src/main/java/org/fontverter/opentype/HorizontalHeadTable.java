package org.fontverter.opentype;

import org.fontverter.io.ByteDataProperty;

public class HorizontalHeadTable extends OpenTypeTable {
    @ByteDataProperty(dataType = ByteDataProperty.DataType.FIXED32)
    public float version;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short ascender;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short descender;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short lineGap;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
    public int advanceWidthMax;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short minLeftSideBearing;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short minRightSideBearing;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short xMaxExtent;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short caretSlopeRise;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short caretSlopeRun;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short caretOffset;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    private short reserved1;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    private short reserved2;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    private short reserved3;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    private short reserved4;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    public short metricDataFormat;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
    public int numberOfHMetrics;

    @Override
    public String getName()
    {
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
        table.numberOfHMetrics = 5;
        return table;
    }
}
