package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.DataTypeProperty;

public class MaximumProfileTable extends OpenTypeTable {
    public static MaximumProfileTable createDefaultTable() {
        MaximumProfileTable table = new MaximumProfileTable();
        table.version = .3125f;
        return table;
    }

    public static MaximumProfileTable createDefaultV1Table() {
        MaximumProfileTable table = new MaximumProfileTable();
        table.version = 1f;
        table.numGlyphs = 1;
        table.maxPoints = 1;
        table.maxContours = 1;
        table.maxCompositePoints = 1;
        table.maxCompositeContours = 1;
        table.maxZones = 1;
        table.maxTwilightPoints = 1;
        table.maxStorage = 1;
        table.maxFunctionDefs = 1;
        table.maxInstructionDefs = 1;
        table.maxStackElements = 1;
        table.maxSizeOfInstructions = 1;
        table.maxComponentElements = 1;
        table.maxComponentDepth = 1;

        return table;
    }

    @DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)
    protected float version;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    protected int numGlyphs;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxPoints;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxContours;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxCompositePoints;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxCompositeContours;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxZones;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxTwilightPoints;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxStorage;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxFunctionDefs;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxInstructionDefs;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxStackElements;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxSizeOfInstructions;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxComponentElements;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, includeIf = "isVersionOne")
    private int maxComponentDepth;

    public String getTableTypeName() {
        return "maxp";
    }

    public boolean isVersionOne() {
        return getVersion() == 1F;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public int getNumGlyphs() {
        return numGlyphs;
    }

    public void setNumGlyphs(int numGlyphs) {
        this.numGlyphs = numGlyphs;
    }
}
