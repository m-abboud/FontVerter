package org.fontverter.opentype;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.fontverter.opentype.OpenTypeProperty.*;

public class HeadTable extends OpenTypeTable
{
    @OpenTypeProperty(dataType = DataType.FIXED32)
    private float version;

    @OpenTypeProperty(dataType = DataType.FIXED32)
    private float fontRevision;

    @OpenTypeProperty(dataType = DataType.ULONG)
    private long checkSumAdjustment;

    @OpenTypeProperty(dataType = DataType.ULONG)
    private long magicNumber;

    @OpenTypeProperty(dataType = DataType.USHORT)
    private int flags;

    @OpenTypeProperty(dataType = DataType.USHORT)
    private int unitsPerEm;

    @OpenTypeProperty(dataType = DataType.LONGDATETIME)
    private Calendar created;

    @OpenTypeProperty(dataType = DataType.LONGDATETIME)
    private Calendar modified;

    @OpenTypeProperty(dataType = DataType.SHORT)
    private short xMin;

    @OpenTypeProperty(dataType = DataType.SHORT)
    private short yMin;

    @OpenTypeProperty(dataType = DataType.SHORT)
    private short xMax;

    @OpenTypeProperty(dataType = DataType.SHORT)
    private short yMax;

    @OpenTypeProperty(dataType = DataType.USHORT)
    private int macStyle;

    @OpenTypeProperty(dataType = DataType.USHORT)
    private int lowestRecPPEM;

    @OpenTypeProperty(dataType = DataType.SHORT)
    private short fontDirectionHint;

    @OpenTypeProperty(dataType = DataType.SHORT)
    private short indexToLocFormat;

    @OpenTypeProperty(dataType = DataType.SHORT)
    private short glyphDataFormat;

    @Override
    public String getName()
    {
        return "head";
    }

    public static HeadTable createEmptyTable()
    {
        HeadTable table = new HeadTable();
        table.version = 1;
        table.fontRevision = 1;
        table.checkSumAdjustment = 1;
        table.magicNumber = 0x5F0F3CF5;
        table.flags = 0;
        table.unitsPerEm = 16;
        table.created = GregorianCalendar.getInstance();
        table.modified = GregorianCalendar.getInstance();
        table.xMin = -100;
        table.yMin = -100;
        table.xMax = 100;
        table.yMax = 100;
        table.macStyle = 0;
        table.lowestRecPPEM = 6;
        table.fontDirectionHint = 2;
        table.indexToLocFormat = 1;
        table.glyphDataFormat = 0;
        return table;
    }
}
