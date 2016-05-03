package org.fontverter.opentype;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.fontverter.opentype.OtfSerializerProperty.*;

public class HeadTable extends OpenTypeTable
{
    @OtfSerializerProperty(dataType = DataType.FIXED32)
    private float version;

    @OtfSerializerProperty(dataType = DataType.FIXED32)
    private float fontRevision;

    @OtfSerializerProperty(dataType = DataType.ULONG)
    private long checkSumAdjustment;

    @OtfSerializerProperty(dataType = DataType.ULONG)
    private long magicNumber;

    @OtfSerializerProperty(dataType = DataType.USHORT)
    private int flags;

    @OtfSerializerProperty(dataType = DataType.USHORT)
    private int unitsPerEm;

    @OtfSerializerProperty(dataType = DataType.LONGDATETIME)
    private Calendar created;

    @OtfSerializerProperty(dataType = DataType.LONGDATETIME)
    private Calendar modified;

    @OtfSerializerProperty(dataType = DataType.SHORT)
    private short xMin;

    @OtfSerializerProperty(dataType = DataType.SHORT)
    private short yMin;

    @OtfSerializerProperty(dataType = DataType.SHORT)
    private short xMax;

    @OtfSerializerProperty(dataType = DataType.SHORT)
    private short yMax;

    @OtfSerializerProperty(dataType = DataType.USHORT)
    private int macStyle;

    @OtfSerializerProperty(dataType = DataType.USHORT)
    private int lowestRecPPEM;

    @OtfSerializerProperty(dataType = DataType.SHORT)
    private short fontDirectionHint;

    @OtfSerializerProperty(dataType = DataType.SHORT)
    private short indexToLocFormat;

    @OtfSerializerProperty(dataType = DataType.SHORT)
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
        table.checkSumAdjustment = 0;
        table.magicNumber = 0x5F0F3CF5;
        table.flags = 11;
        table.unitsPerEm = 100;
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


    public void checksumAdjustment(byte[] fontBytes) throws IOException {
        checkSumAdjustment = 0xB1B0AFBA - getTableChecksum(fontBytes);
    }
}
