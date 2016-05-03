package org.fontverter.opentype;

public class PostScriptTable extends OpenTypeTable
{
    @OtfDataProperty(dataType = OtfDataProperty.DataType.FIXED32)
    private float formatType;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.FIXED32)
    private float italicAngle;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    private short underlinePosition;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.SHORT)
    private short underlineThickness;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.ULONG)
    private long isFixedPitch;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.ULONG)
    private long minMemType42;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.ULONG)
    private long maxMemType42;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.ULONG)
    private long mimMemType1;

    @OtfDataProperty(dataType = OtfDataProperty.DataType.ULONG)
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
