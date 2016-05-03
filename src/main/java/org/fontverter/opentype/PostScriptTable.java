package org.fontverter.opentype;

public class PostScriptTable extends OpenTypeTable
{
    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.FIXED32)
    private float formatType;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.FIXED32)
    private float italicAngle;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    private short underlinePosition;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.SHORT)
    private short underlineThickness;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.ULONG)
    private long isFixedPitch;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.ULONG)
    private long minMemType42;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.ULONG)
    private long maxMemType42;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.ULONG)
    private long mimMemType1;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.ULONG)
    private long maxMemType1;

    private String[] glyphNames = null;

    @Override
    public String getName()
    {
        return "post";
    }

    public static PostScriptTable createEmptyTable()
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
