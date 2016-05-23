package org.fontverter.opentype;

import org.fontverter.io.DataTypeProperty;

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
    public String getTableTypeName() {
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
