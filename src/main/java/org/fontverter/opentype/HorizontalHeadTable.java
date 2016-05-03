package org.fontverter.opentype;

public class HorizontalHeadTable extends OpenTypeTable {
    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.FIXED32)
    public float version;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short ascender;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short descender;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short lineGap;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.USHORT)
    public int advanceWidthMax;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short minLeftSideBearing;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short minRightSideBearing;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short xMaxExtent;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short caretSlopeRise;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short caretSlopeRun;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short caretOffset;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    private short reserved1;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    private short reserved2;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    private short reserved3;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    private short reserved4;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    public short metricDataFormat;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.USHORT)
    public int numberOfHMetrics;

    @Override
    public String getName()
    {
        return "hhea";
    }

    public static HorizontalHeadTable createEmptyTable() {
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
