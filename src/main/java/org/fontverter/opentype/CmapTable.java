package org.fontverter.opentype;


import org.fontverter.FontWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.fontverter.opentype.CmapTable.CmapSubTable.CMAP_RECORD_BYTE_SIZE;

public class CmapTable extends OpenTypeTable
{
    private static Logger log = LoggerFactory.getLogger(CmapTable.class);
    private static final int CMAP_HEADER_SIZE = 4;
    private List<CmapSubTable> subTables = new ArrayList<CmapSubTable>();

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    int version;

    @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
    int numTables()
    {
        return subTables.size();
    }


    @Override
    public String getName()
    {
        return "cmap";
    }

    @Override
    public byte[] getData() throws IOException
    {
        calculateOffsets();

        FontWriter writer = FontWriter.createWriter();
        writer.write(super.getData());

        for (CmapSubTable tableOn : subTables)
        {
            writer.write(tableOn.getRecordData());
        }

        for (CmapSubTable tableOn : subTables)
        {
            OpenTypeTableSerializer serializer = new OpenTypeTableSerializer();
            writer.write(serializer.serialize(tableOn));

        }
        return writer.toByteArray();
    }

    public static CmapTable createEmptyTable()
    {
        CmapTable table = new CmapTable();
        table.version = 0;
        return table;
    }


    private void calculateOffsets() throws IOException
    {
        int offset = subTables.size() * CMAP_RECORD_BYTE_SIZE + CMAP_HEADER_SIZE;
        for (CmapSubTable tableOn : subTables)
        {
            tableOn.setSubTableOffset(offset);
            log.debug("{} Cmap sub table Offset Calc: ", offset);

            offset += tableOn.getData().length;
        }
    }

    protected static abstract class CmapSubTable
    {
        public static final int CMAP_RECORD_BYTE_SIZE = 8;

        private int platformId;
        private int platformEncodingId;
        private long subTableOffset;
        private int[] glyphIdToCharacterCode;
        private Map<Integer, Integer> characterCodeToGlyphId;

        protected float formatNumber;

        public long getSubTableOffset()
        {
            return subTableOffset;
        }

        public void setSubTableOffset(long subTableOffset)
        {
            this.subTableOffset = subTableOffset;
        }

        public byte[] getRecordData() throws IOException
        {
            FontWriter writer = FontWriter.createWriter();
            writer.writeUnsignedShort(platformId);
            writer.writeUnsignedShort(platformEncodingId);
            writer.writeUnsignedInt((int) subTableOffset);
            return writer.toByteArray();
        }


        public abstract byte[] getData() throws IOException;

    }

    protected static class Format4SubTable extends CmapSubTable
    {
        public Format4SubTable()
        {
            formatNumber = 4;
        }

        @Override
        public byte[] getData() throws IOException
        {
            FontWriter writer = FontWriter.createWriter();
            writer.writeUnsignedShort((int) formatNumber);

            return writer.toByteArray();
        }
    }

    protected static class SubTableSegment
    {
        int endCount;
        int startCount;
        int idCount;
        int idRangeOffset;
    }
}
