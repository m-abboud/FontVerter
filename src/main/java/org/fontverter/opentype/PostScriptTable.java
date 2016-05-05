package org.fontverter.opentype;

import org.fontverter.io.ByteDataProperty;

public class PostScriptTable extends OpenTypeTable
{
    @ByteDataProperty(dataType = ByteDataProperty.DataType.FIXED32)
    private float formatType;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.FIXED32)
    private float italicAngle;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    private short underlinePosition;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    private short underlineThickness;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.ULONG)
    private long isFixedPitch;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.ULONG)
    private long minMemType42;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.ULONG)
    private long maxMemType42;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.ULONG)
    private long mimMemType1;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.ULONG)
    private long maxMemType1;

    private String[] glyphNames = null;

    @Override
    public String getName()
    {
        return "post";
    }

    public static PostScriptTable createDefaultTable()
    {
        PostScriptTable table = new PostScriptTable();
        table.formatType = 3.0f;
        table.italicAngle = 0.0f;
        table.underlinePosition = -143;
        table.underlineThickness = 20;
        table.isFixedPitch = 0;
        table.minMemType42 = 0;
        table.maxMemType42 = 0;
        table.mimMemType1 = 0;
        table.maxMemType1 = 0;
        table.glyphNames = null;
        return table;
    }
}
