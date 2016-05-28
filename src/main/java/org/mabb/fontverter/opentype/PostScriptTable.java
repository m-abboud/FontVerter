package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.DataTypeProperty;

public class PostScriptTable extends OpenTypeTable {
    @DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)
    private float version;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)
    private float italicAngle;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short underlinePosition;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    private short underlineThickness;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.ULONG)
    private long isFixedPitch;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.ULONG)
    private long minMemType42;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.ULONG)
    private long maxMemType42;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.ULONG)
    private long mimMemType1;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.ULONG)
    private long maxMemType1;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersion2")
    private int numGlyphs = 0;
//
//    @DataTypeProperty(dataType = DataTypeProperty.DataType.ULONG)
    private String[] glyphNames = null;

    @Override
    public String getTableTypeName() {
        return "post";
    }

    public static PostScriptTable createDefaultTable(float version) {
        PostScriptTable table = new PostScriptTable();
        table.version = version;
        table.italicAngle = 0.0f;
        table.underlinePosition = -143;
        table.underlineThickness = 20;
        table.isFixedPitch = 0;
        table.minMemType42 = 0;
        table.maxMemType42 = 0;
        table.mimMemType1 = 0;
        table.maxMemType1 = 0;

        if (version == 2) {
            // todo
            table.glyphNames = null;
        }

        return table;
    }

    public float getVersion() {
        return version;
    }

    public float getItalicAngle() {
        return italicAngle;
    }

    public short getUnderlinePosition() {
        return underlinePosition;
    }

    public short getUnderlineThickness() {
        return underlineThickness;
    }

    public long getIsFixedPitch() {
        return isFixedPitch;
    }

    public long getMinMemType42() {
        return minMemType42;
    }

    public long getMaxMemType42() {
        return maxMemType42;
    }

    public long getMimMemType1() {
        return mimMemType1;
    }

    public long getMaxMemType1() {
        return maxMemType1;
    }

    public boolean isVersion2() {
        return getVersion() == 2;
    }

    void normalize() {
        numGlyphs = font.getCmap().getGlyphCount();

        super.normalize();
    }
}
