package org.mabb.fontverter.opentype;


import org.mabb.fontverter.io.FontDataOutputStream;
import org.mabb.fontverter.io.DataTypeProperty;
import org.mabb.fontverter.io.DataTypeSerializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.mabb.fontverter.opentype.CmapTable.CmapSubTable.CMAP_RECORD_BYTE_SIZE;

public class CmapTable extends OpenTypeTable {
    private static Logger log = LoggerFactory.getLogger(CmapTable.class);
    private static final int CMAP_HEADER_SIZE = 4;
    private Format4SubTable windowsTable;
    private Format4SubTable unixTable;
    private Format0SubTable macTable;

    private List<CmapSubTable> subTables = new ArrayList<CmapSubTable>();

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    int version;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    int numTables() {
        return subTables.size();
    }

    /* big old kludge to handle conversion of tables types that arn't deserializable/parsable yet remove asap*/
    protected boolean isParsingImplemented() {
        return false;
    }

    public String getTableTypeName() {
        return "cmap";
    }

    @Override
    protected byte[] generateUnpaddedData() throws IOException {
        calculateOffsets();

        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        writer.write(super.generateUnpaddedData());

        for (CmapSubTable tableOn : subTables) {
            writer.write(tableOn.getRecordData());
        }

        for (CmapSubTable tableOn : subTables)
            writer.write(tableOn.getData());

        return writer.toByteArray();
    }

    public static CmapTable createDefaultTable() {
        CmapTable table = new CmapTable();
        table.version = 0;

        table.unixTable = new Format4SubTable();
        table.unixTable.setPlatformId(0);
        table.unixTable.setEncodingId(3);
        table.subTables.add(table.unixTable);

        table.macTable = new Format0SubTable();
        table.macTable.setPlatformId(1);
        table.macTable.setEncodingId(0);
        table.subTables.add(table.macTable);

        table.windowsTable = new Format4SubTable();
        table.windowsTable.setPlatformId(3);
        table.windowsTable.setEncodingId(1);
        table.subTables.add(table.windowsTable);

        return table;
    }

    public void addGlyphMapping(Integer charCode, Integer glyphId) {
        windowsTable.addGlyphMapping(charCode, glyphId);
        unixTable.addGlyphMapping(charCode, glyphId);
    }

    public void addGlyphMapping(Map<Integer, Integer> mapping) {
        for (Map.Entry<Integer, Integer> mappingOn : mapping.entrySet()) {
            int charCode = mappingOn.getKey();
            int glyphId = mappingOn.getValue();

            addGlyphMapping(charCode, glyphId);
        }
    }

    public int getGlyphCount() {
        if (subTables.size() == 0)
            return 0;
        return subTables.get(0).glyphCount();
    }

    private void calculateOffsets() throws IOException {
        int offset = subTables.size() * CMAP_RECORD_BYTE_SIZE + CMAP_HEADER_SIZE;
        for (CmapSubTable tableOn : subTables) {
            tableOn.setSubTableOffset(offset);
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
            FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
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
        private int length = 0;

        public Format4SubTable() {
            formatNumber = 4;
        }

        @Override
        public byte[] getData() throws IOException {
            FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);

            writer.writeUnsignedShort((int) formatNumber);
            writer.writeUnsignedShort(getLength());
            writer.writeUnsignedShort(getLanguageId());

            writer.writeUnsignedShort(getSegmentCount() * 2);
            writer.writeUnsignedShort(getSearchRange());
            writer.writeUnsignedShort(getEntrySelector());
            writer.writeUnsignedShort(getRangeShift());


            List<Integer> ends = getGlyphEnds();
            List<Integer> starts = getGlyphStarts();
            List<Integer> deltas = getGlyphDeltas();

            for (Integer endEntryOn : ends)
                writer.writeUnsignedShort(endEntryOn);
            // end[] padding
            writer.writeUnsignedShort(65535);

            // 'reservedPad' Set to 0
            writer.writeUnsignedShort(0);

            for (Integer startEntryOn : starts)
                writer.writeUnsignedShort(startEntryOn);
            // start[] padding,
            writer.writeUnsignedShort(65535);

            // idDelta[], delta is glyphId storing
            for (Integer deltaEntryOn : deltas)
                writer.writeUnsignedShort(deltaEntryOn);
            writer.writeUnsignedShort(1);

            // idRangeOffset[] blanks unused
            for (int i = 0; i < getSegmentCount(); i++)
                writer.writeUnsignedInt(0);


            byte[] data = writer.toByteArray();
            setDataHeaderLength(data);
            return data;
        }

        private void setDataHeaderLength(byte[] data) throws IOException {
            FontDataOutputStream lengthWriter = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
            lengthWriter.writeUnsignedShort(data.length);
            byte[] lengthData = lengthWriter.toByteArray();
            data[2] = lengthData[0];
            data[3] = lengthData[1];
        }

        public int glyphCount() {
            return charCodeToGlyphId.size() + 1;
        }

        private int getLanguageId() {
            return 0;
        }

        private int getSegmentCount() {
            // +1 for padding at end of segment arrays
            return getGlyphEnds().size() + 1;
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
            return FORMAT4_HEADER_SIZE + ((charCodeToGlyphId.size()) * 8);
        }

        public void addGlyphMapping(int characterCode, int glyphId) {
            charCodeToGlyphId.put(characterCode, glyphId);
        }

        private List<Integer> getGlyphDeltas() {
            List<Integer> deltas = new ArrayList<Integer>();

            int lastCharCode = -1;
            for (Map.Entry<Integer, Integer> entryOn : getOrderedCharCodeToGlyphIds()) {
                int curCharCode = entryOn.getKey();
                if (curCharCode != lastCharCode + 1)
                    deltas.add(65536 + entryOn.getValue() - curCharCode);

                lastCharCode = curCharCode;
            }

            return deltas;
        }

        private List<Integer> getGlyphStarts() {
            List<Integer> starts = new ArrayList<Integer>();
            int lastCharCode = -1;

            for (Map.Entry<Integer, Integer> entryOn : getOrderedCharCodeToGlyphIds()) {
                int curCharCode = entryOn.getKey();
                if (curCharCode != lastCharCode + 1)
                    starts.add(curCharCode);

                lastCharCode = curCharCode;
            }

            return starts;
        }

        private List<Integer> getGlyphEnds() {
            List<Integer> ends = new ArrayList<Integer>();
            int lastCharCode = -1;
            List<Map.Entry<Integer, Integer>> entries = getOrderedCharCodeToGlyphIds();
            for (Map.Entry<Integer, Integer> entryOn : entries) {
                int curCharCode = entryOn.getKey();
                if (curCharCode != lastCharCode + 1 && lastCharCode != -1)
                    ends.add(lastCharCode);

                lastCharCode = curCharCode;
            }

            // add last one not caught in loop
            if (entries.size() > 1)
                ends.add(entries.get(entries.size() - 1).getKey());

            return ends;
        }

        private List<Map.Entry<Integer, Integer>> getOrderedCharCodeToGlyphIds() {
            List<Map.Entry<Integer, Integer>> charCodeEntries = new ArrayList<Map.Entry<Integer, Integer>>();
            for (Map.Entry<Integer, Integer> entryOn : charCodeToGlyphId.entrySet())
                charCodeEntries.add(entryOn);

            Collections.sort(charCodeEntries, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                    return o1.getKey() < o2.getKey() ? -1 : o1.getKey().equals(o2.getKey()) ? 0 : 1;
                }
            });

            return charCodeEntries;
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
            FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
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
}
