package org.fontverter.opentype;

public class HorizontalHeadTable extends OpenTypeTable {
    @OtfDataProperty(dataType = OtfDataProperty.DataType.FIXED32)
    public float version;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short ascender;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short descender;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short lineGap;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.USHORT)
    public int advanceWidthMax;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short minLeftSideBearing;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short minRightSideBearing;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short xMaxExtent;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short caretSlopeRise;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short caretSlopeRun;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short caretOffset;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    private short reserved1;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    private short reserved2;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    private short reserved3;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    private short reserved4;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    public short metricDataFormat;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.USHORT)
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
