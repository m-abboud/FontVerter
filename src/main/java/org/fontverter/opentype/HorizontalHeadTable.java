package org.fontverter.opentype;

public class HorizontalHeadTable extends OpenTypeTable {
    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.FIXED32)
    private float version;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short ascender;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short descender;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short lineGap;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int advanceWidthMax;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short minLeftSideBearing;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short minRightSideBearing;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short xMaxExtent;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short caretSlopeRise;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short caretSlopeRun;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short reserved1;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short reserved2;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short reserved3;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short reserved4;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short reserved5;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short metricDataFormat;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int numberOfHMetrics;

    @Override
    public String getName()
    {
        return "hhea";
    }

    public static HorizontalHeadTable createEmptyTable() {
        HorizontalHeadTable table = new HorizontalHeadTable();
        table.version = 1;
        table.ascender = 1;
        table.descender = 1;
        table.lineGap = 1;
        table.advanceWidthMax = 1;
        table.minLeftSideBearing = 1;
        table.minRightSideBearing = 1;
        table.xMaxExtent = 1;
        table.caretSlopeRise = 1;
        table.caretSlopeRun = 1;
        table.reserved1 = 1;
        table.reserved2 = 1;
        table.reserved3 = 1;
        table.reserved4 = 1;
        table.reserved5 = 1;
        table.metricDataFormat = 1;
        table.numberOfHMetrics = 1;
        return table;
    }
}
