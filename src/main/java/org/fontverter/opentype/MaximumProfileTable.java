package org.fontverter.opentype;

import org.fontverter.io.ByteDataProperty;

public class MaximumProfileTable extends OpenTypeTable {
    public static MaximumProfileTable createDefaultTable() {
        MaximumProfileTable table = new MaximumProfileTable();
        table.version = .3125f;
        return table;
    }

    @ByteDataProperty(dataType = ByteDataProperty.DataType.FIXED32)
    protected float version;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
    protected int numGlyphs;

    @Override
    public String getName() {
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

    public static class MaximumProfileTableV1 extends MaximumProfileTable {
        public static MaximumProfileTable createDefaultTable() {
            MaximumProfileTableV1 table = new MaximumProfileTableV1();
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

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxPoints;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxContours;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxCompositePoints;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxCompositeContours;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxZones;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxTwilightPoints;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxStorage;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxFunctionDefs;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxInstructionDefs;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxStackElements;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxSizeOfInstructions;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxComponentElements;

        @ByteDataProperty(dataType = ByteDataProperty.DataType.USHORT)
        private int maxComponentDepth;

        public int getMaxPoints() {
            return maxPoints;
        }

        public void setMaxPoints(int maxPoints) {
            this.maxPoints = maxPoints;
        }

        public int getMaxContours() {
            return maxContours;
        }

        public void setMaxContours(int maxContours) {
            this.maxContours = maxContours;
        }

        public int getMaxCompositePoints() {
            return maxCompositePoints;
        }

        public void setMaxCompositePoints(int maxCompositePoints) {
            this.maxCompositePoints = maxCompositePoints;
        }

        public int getMaxCompositeContours() {
            return maxCompositeContours;
        }

        public void setMaxCompositeContours(int maxCompositeContours) {
            this.maxCompositeContours = maxCompositeContours;
        }

        public int getMaxZones() {
            return maxZones;
        }

        public void setMaxZones(int maxZones) {
            this.maxZones = maxZones;
        }

        public int getMaxTwilightPoints() {
            return maxTwilightPoints;
        }

        public void setMaxTwilightPoints(int maxTwilightPoints) {
            this.maxTwilightPoints = maxTwilightPoints;
        }

        public int getMaxStorage() {
            return maxStorage;
        }

        public void setMaxStorage(int maxStorage) {
            this.maxStorage = maxStorage;
        }

        public int getMaxFunctionDefs() {
            return maxFunctionDefs;
        }

        public void setMaxFunctionDefs(int maxFunctionDefs) {
            this.maxFunctionDefs = maxFunctionDefs;
        }

        public int getMaxInstructionDefs() {
            return maxInstructionDefs;
        }

        public void setMaxInstructionDefs(int maxInstructionDefs) {
            this.maxInstructionDefs = maxInstructionDefs;
        }

        public int getMaxStackElements() {
            return maxStackElements;
        }

        public void setMaxStackElements(int maxStackElements) {
            this.maxStackElements = maxStackElements;
        }

        public int getMaxSizeOfInstructions() {
            return maxSizeOfInstructions;
        }

        public void setMaxSizeOfInstructions(int maxSizeOfInstructions) {
            this.maxSizeOfInstructions = maxSizeOfInstructions;
        }

        public int getMaxComponentElements() {
            return maxComponentElements;
        }

        public void setMaxComponentElements(int maxComponentElements) {
            this.maxComponentElements = maxComponentElements;
        }

        public int getMaxComponentDepth() {
            return maxComponentDepth;
        }

        public void setMaxComponentDepth(int maxComponentDepth) {
            this.maxComponentDepth = maxComponentDepth;
        }
    }
}
