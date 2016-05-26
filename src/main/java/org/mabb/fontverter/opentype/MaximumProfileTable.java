package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.DataTypeProperty;

public class MaximumProfileTable extends OpenTypeTable {
    public static MaximumProfileTable createDefaultTable() {
        MaximumProfileTable table = new MaximumProfileTable();
        table.version = .3125f;
        return table;
    }

    @DataTypeProperty(dataType = DataTypeProperty.DataType.FIXED32)
    protected float version;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    protected int numGlyphs;

    @Override
    public String getTableTypeName() {
        return "maxp";
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

    protected boolean isParsingImplemented() {
        return false;
    }

    // todo: commented out since deserializer inhertiance broken
//    public static class MaximumProfileTableV1 extends MaximumProfileTable {
//        public static MaximumProfileTable createDefaultTable() {
//            MaximumProfileTableV1 table = new MaximumProfileTableV1();
//            table.version = 1f;
//            table.numGlyphs = 1;
//            table.maxPoints = 1;
//            table.maxContours = 1;
//            table.maxCompositePoints = 1;
//            table.maxCompositeContours = 1;
//            table.maxZones = 1;
//            table.maxTwilightPoints = 1;
//            table.maxStorage = 1;
//            table.maxFunctionDefs = 1;
//            table.maxInstructionDefs = 1;
//            table.maxStackElements = 1;
//            table.maxSizeOfInstructions = 1;
//            table.maxComponentElements = 1;
//            table.maxComponentDepth = 1;
//
//            return table;
//        }
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxPoints;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxContours;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxCompositePoints;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxCompositeContours;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxZones;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxTwilightPoints;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxStorage;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxFunctionDefs;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxInstructionDefs;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxStackElements;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxSizeOfInstructions;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxComponentElements;
//
//        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
//        private int maxComponentDepth;
//    }
}
