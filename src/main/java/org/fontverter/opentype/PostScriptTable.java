package org.fontverter.opentype;

public class PostScriptTable extends OpenTypeTable
{
    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.FIXED32)
    private float formatType;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.FIXED32)
    private float italicAngle;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short underlinePosition;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.SHORT)
    private short underlineThickness;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.ULONG)
    private long isFixedPitch;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.ULONG)
    private long minMemType42;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.ULONG)
    private long maxMemType42;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.ULONG)
    private long mimMemType1;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.ULONG)
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
