package org.fontverter.opentype;


import org.fontverter.FontWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.fontverter.opentype.CmapTable.CmapSubTable.CMAP_RECORD_BYTE_SIZE;

public class CmapTable extends OpenTypeTable {
    private static Logger log = LoggerFactory.getLogger(CmapTable.class);
    private static final int CMAP_HEADER_SIZE = 4;
    private static Format4SubTable windowsTable;
    private static Format4SubTable uniTable;
    private List<CmapSubTable> subTables = new ArrayList<CmapSubTable>();
    private static Format0SubTable macTable;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.USHORT)
    int version;

    @OtfSerializerProperty(dataType = OtfSerializerProperty.DataType.USHORT)
    int numTables() {
        return subTables.size();
    }


    @Override
    public String getName() {
        return "cmap";
    }

    @Override
    protected byte[] getRawData() throws IOException, FontSerializerException {
        calculateOffsets();

        FontWriter writer = FontWriter.createWriter();
        writer.write(super.getRawData());

        for (CmapSubTable tableOn : subTables) {
            writer.write(tableOn.getRecordData());
        }

        for (CmapSubTable tableOn : subTables)
            writer.write(tableOn.getData());

        return writer.toByteArray();
    }

    public static CmapTable createEmptyTable() {
        CmapTable table = new CmapTable();
        table.version = 0;

        uniTable = new Format4SubTable();
        uniTable.setPlatformId(0);
        uniTable.setEncodingId(3);
        table.subTables.add(uniTable);

        macTable = new Format0SubTable();
        macTable.setPlatformId(1);
        macTable.setEncodingId(0);
        table.subTables.add(macTable);

        windowsTable = new Format4SubTable();
        windowsTable.setPlatformId(3);
        windowsTable.setEncodingId(1);
        table.subTables.add(windowsTable);

        return table;
    }

    public void addGlyphMapping(Integer charCode, Integer glyphId) {
        windowsTable.addGlyphMapping(charCode, glyphId);
        uniTable.addGlyphMapping(charCode, glyphId);
    }

    public void addGlyphMapping(Map<Integer, Integer> mapping) {
        for (Map.Entry<Integer, Integer> mappingOn : mapping.entrySet()) {
            int charCode = mappingOn.getKey();
            int glyphId = mappingOn.getValue();

            addGlyphMapping(charCode, glyphId);
        }
    }

    public int getNumberOfGlyphs() {
        if (subTables.size() == 0)
            return 0;
        return subTables.get(0).glyphCount();
    }


    private void calculateOffsets() throws IOException {
        int offset = subTables.size() * CMAP_RECORD_BYTE_SIZE + CMAP_HEADER_SIZE;
        for (CmapSubTable tableOn : subTables) {
            tableOn.setSubTableOffset(offset);
            log.debug("{} Cmap sub table Offset Calc: ", offset);

            offset += tableOn.getData().length;
        }
    }

    protected static abstract class CmapSubTable {
        public static final int CMAP_RECORD_BYTE_SIZE = 8;

        private int platformId;
        private int platformEncodingId;
        private long subTableOffset;
        private int[] glyphIdToCharacterCode;
        private Map<Integer, Integer> characterCodeToGlyphId;

        protected float formatNumber;

        public long getSubTableOffset() {
            return subTableOffset;
        }

        public void setSubTableOffset(long subTableOffset) {
            this.subTableOffset = subTableOffset;
        }

        public byte[] getRecordData() throws IOException {
            FontWriter writer = FontWriter.createWriter();
            writer.writeUnsignedShort(platformId);
            writer.writeUnsignedShort(platformEncodingId);
            writer.writeUnsignedInt((int) subTableOffset);
            return writer.toByteArray();
        }

        public int getPlatformId() {
            return platformId;
        }

        public void setPlatformId(int platformId) {
            this.platformId = platformId;
        }

        public int getPlatformEncodingId() {
            return platformEncodingId;
        }

        public void setEncodingId(int platformEncodingId) {
            this.platformEncodingId = platformEncodingId;
        }

        public abstract byte[] getData() throws IOException;

        public abstract int glyphCount();

    }

    protected static class Format4SubTable extends CmapSubTable {
        private static final int FORMAT4_HEADER_SIZE = 16;
        // LinkedHashMap important, for keeping ordering the same for loops
        private LinkedHashMap<Integer, Integer> charCodeToGlyphId = new LinkedHashMap<Integer, Integer>();

        public Format4SubTable() {
            formatNumber = 4;
        }

        @Override
        public byte[] getData() throws IOException {
            FontWriter writer = FontWriter.createWriter();

            writer.writeUnsignedShort((int) formatNumber);
            writer.writeUnsignedShort(getLength());
            writer.writeUnsignedShort(getLanguageId());

            writer.writeUnsignedShort(getSegmentCount() * 2);
            writer.writeUnsignedShort(getSearchRange());
            writer.writeUnsignedShort(getEntrySelector());
            writer.writeUnsignedShort(getRangeShift());


            List<Map.Entry<Integer, Integer>> orderedCodeMaps = getOrderedCharCodeToGlyphIds();

            // end[], end == charactercode in our current simple case
            for (Map.Entry<Integer, Integer> entry : orderedCodeMaps)
                writer.writeUnsignedShort(entry.getKey());
            // end[] padding
            writer.writeUnsignedShort(65535);

            // 'reservedPad' Set to 0
            writer.writeUnsignedShort(0);

            // start[] and padding, start == charactercode in our current simple case
            for (Map.Entry<Integer, Integer> entry : orderedCodeMaps)
                writer.writeUnsignedShort(entry.getKey());
            writer.writeUnsignedShort(65535);

            // idDelta[], delta is glyphId storing
            for (Map.Entry<Integer, Integer> entry : orderedCodeMaps) {
                int delta = 65536 + entry.getValue() - entry.getKey();
                writer.writeUnsignedShort(delta);
            }
            writer.writeUnsignedShort(1);

            // idRangeOffset[] blanks unused
            for (int i = 0; i < getSegmentCount() + 1; i++)
                writer.writeUnsignedInt(0);


            return writer.toByteArray();
        }

        private List<Map.Entry<Integer, Integer>> getOrderedCharCodeToGlyphIds() {
            List<Map.Entry<Integer, Integer>> charCodeEntries = new ArrayList<Map.Entry<Integer, Integer>>();
            for (Map.Entry<Integer, Integer> entryOn : charCodeToGlyphId.entrySet())
                charCodeEntries.add(entryOn);

            Collections.sort(charCodeEntries, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                    return o1.getValue() < o2.getValue() ? -1 : o1.getValue().equals(o2.getValue()) ? 0 : 1;
                }
            });

            return charCodeEntries;
        }

        @Override
        public int glyphCount() {
            return getSegmentCount();
        }

        private int getLanguageId() {
            return 0;
        }

        private int getSegmentCount() {
            // +1 for padding at end of segment arrays
            return charCodeToGlyphId.size() + 1;
        }

        private int getSearchRange() {
            double logFloor = Math.floor(log2(getSegmentCount()));
            return (int) (2 * (Math.pow(2, logFloor)));
        }

        private int getEntrySelector() {
            return (int) log2(getSearchRange() / 2);
        }

        private int getRangeShift() {
            return 2 * getSegmentCount() - getSearchRange();
        }

        private double log2(int number) {
            return Math.log(number) / Math.log(2);
        }

        private int getLength() {
            // + 1 to size for appearant padding need
            return FORMAT4_HEADER_SIZE + ((charCodeToGlyphId.size() + 1) * 8);
        }

        public void addGlyphMapping(int characterCode, int glyphId) {
            charCodeToGlyphId.put(characterCode, glyphId);
        }

    }

    protected static class Format0SubTable extends CmapSubTable {
        private static final int FORMAT0_HEADER_SIZE = 6 + 256;
        // LinkedHashMap important, for keeping ordering the same for loops
        private LinkedHashMap<Integer, Integer> charCodeToGlyphId = new LinkedHashMap<Integer, Integer>();

        public Format0SubTable() {
            formatNumber = 0;
            for (int i = 0; i < 256; i++)
                charCodeToGlyphId.put(i, 0);
        }

        @Override
        public byte[] getData() throws IOException {
            FontWriter writer = FontWriter.createWriter();

            writer.writeUnsignedShort((int) formatNumber);
            writer.writeUnsignedShort(getLength());
            writer.writeUnsignedShort(getLanguageId());

            for (Map.Entry<Integer, Integer> entry : charCodeToGlyphId.entrySet()) {
                writer.writeByte(entry.getValue());
            }

            return writer.toByteArray();
        }

        public int glyphCount() {
            return 0;
        }

        private int getLanguageId() {
            return 0;
        }

        private int getLength() {
            // + 1 to size for appearant padding need
            return FORMAT0_HEADER_SIZE;
        }

        public void addGlyphMapping(int characterCode, int glyphId) {
            charCodeToGlyphId.put(characterCode, glyphId);
        }

    }

    protected static class SubTableSegment {
        int endCount;
        int startCount;
        int idCount;
        int idRangeOffset;
    }
}
