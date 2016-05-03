package org.fontverter.opentype;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.fontverter.opentype.OtfDataProperty.*;

public class HeadTable extends OpenTypeTable
{
    @OtfDataProperty(dataType = DataType.FIXED32)
    private float version;

    @OtfDataProperty(dataType = DataType.FIXED32)
    private float fontRevision;

    @OtfDataProperty(dataType = DataType.ULONG)
    private long checkSumAdjustment;

    @OtfDataProperty(dataType = DataType.ULONG)
    private long magicNumber;

    @OtfDataProperty(dataType = DataType.USHORT)
    private int flags;

    @OtfDataProperty(dataType = DataType.USHORT)
    private int unitsPerEm;

    @OtfDataProperty(dataType = DataType.LONGDATETIME)
    private Calendar created;

    @OtfDataProperty(dataType = DataType.LONGDATETIME)
    private Calendar modified;

    @OtfDataProperty(dataType = DataType.SHORT)
    private short xMin;

    @OtfDataProperty(dataType = DataType.SHORT)
    private short yMin;

    @OtfDataProperty(dataType = DataType.SHORT)
    private short xMax;

    @OtfDataProperty(dataType = DataType.SHORT)
    private short yMax;

    @OtfDataProperty(dataType = DataType.USHORT)
    private int macStyle;

    @OtfDataProperty(dataType = DataType.USHORT)
    private int lowestRecPPEM;

    @OtfDataProperty(dataType = DataType.SHORT)
    private short fontDirectionHint;

    @OtfDataProperty(dataType = DataType.SHORT)
    private short indexToLocFormat;

    @OtfDataProperty(dataType = DataType.SHORT)
    private short glyphDataFormat;

    public String getName()
    {
        return "head";
    }

    public static HeadTable createDefaultTable()
    {
        HeadTable table = new HeadTable();
        table.version = 1;
        table.fontRevision = 1;
        table.checkSumAdjustment = 0;
        table.magicNumber = 0x5F0F3CF5;
        table.flags = 11;
        table.unitsPerEm = 1000;
        table.created = GregorianCalendar.getInstance();
        table.modified = GregorianCalendar.getInstance();
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
        checkSumAdjustment = 0xB1B0AFBA - getTableChecksum(fontBytes);
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
}
