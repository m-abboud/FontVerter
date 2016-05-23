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

    public String getTableTypeName() {
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

        table.created = GregorianCalendar.getInstance();
        table.created.set(1991, 3, 1);
        table.modified = GregorianCalendar.getInstance();
        table.modified.set(1991, 3, 1);

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
}
