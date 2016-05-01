package org.fontverter.opentype;

public class MaximumProfileTable extends OpenTypeTable
{
    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.FIXED32)
    private float version;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int numGlyphs;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxPoints;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxContours;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxCompositePoints;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxCompositeContours;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxZones;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxTwilightPoints;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxStorage;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxFunctionDefs;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxInstructionDefs;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxStackElements;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxSizeOfInstructions;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxComponentElements;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    private int maxComponentDepth;

    @Override
    public String getName()
    {
        return "maxp";
    }

    public static MaximumProfileTable createEmptyTable() {
        MaximumProfileTable table = new MaximumProfileTable();
        table.version = 1;
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
}
