package org.fontverter.opentype;

import org.fontverter.FontVerterUtils;
import org.fontverter.io.ByteDataProperty;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.fontverter.io.ByteDataProperty.*;

public class HeadTable extends OpenTypeTable
{
    @ByteDataProperty(dataType = DataType.FIXED32)
    private float version;

    @ByteDataProperty(dataType = DataType.FIXED32)
    private float fontRevision;

    @ByteDataProperty(dataType = DataType.ULONG)
    private long checkSumAdjustment;

    @ByteDataProperty(dataType = DataType.ULONG)
    private long magicNumber;

    @ByteDataProperty(dataType = DataType.USHORT)
    private int flags;

    @ByteDataProperty(dataType = DataType.USHORT)
    private int unitsPerEm;

    @ByteDataProperty(dataType = DataType.LONGDATETIME)
    private Calendar created;

    @ByteDataProperty(dataType = DataType.LONGDATETIME)
    private Calendar modified;

    @ByteDataProperty(dataType = DataType.SHORT)
    private short xMin;

    @ByteDataProperty(dataType = DataType.SHORT)
    private short yMin;

    @ByteDataProperty(dataType = DataType.SHORT)
    private short xMax;

    @ByteDataProperty(dataType = DataType.SHORT)
    private short yMax;

    @ByteDataProperty(dataType = DataType.USHORT)
    private int macStyle;

    @ByteDataProperty(dataType = DataType.USHORT)
    private int lowestRecPPEM;

    @ByteDataProperty(dataType = DataType.SHORT)
    private short fontDirectionHint;

    @ByteDataProperty(dataType = DataType.SHORT)
    private short indexToLocFormat;

    @ByteDataProperty(dataType = DataType.SHORT)
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
