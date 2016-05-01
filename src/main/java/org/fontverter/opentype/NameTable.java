package org.fontverter.opentype;

import org.fontverter.FontWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.fontverter.FontWriter.openTypeCharEncoding;
import static org.fontverter.opentype.NameTable.NameRecord.NAME_RECORD_SIZE;

public class NameTable extends OpenTypeTable
{
    private static Logger log = LoggerFactory.getLogger(NameTable.class);
    static final int NAME_TABLE_HEADER_SIZE = 6;
    private List<NameRecord> nameRecords = new ArrayList<NameRecord>();
    private int formatSelector = 1;

    public static NameTable createTable(String[] names)
    {
        NameTable table = new NameTable();
        for(String nameOn : names)
            table.nameRecords.add(new NameRecord(nameOn));

        return table;
    }

    @Override
    public byte[] getData() throws IOException
    {
        FontWriter writer = FontWriter.createWriter();
        writer.writeUnsignedInt(formatSelector);
        writer.writeUnsignedInt(nameRecords.size());
        writer.writeUnsignedInt(getOffsetToStringStorage());

        calculateOffsets();

        for (NameRecord record : nameRecords)
            writer.write(record.getRecordData());

        for (NameRecord record : nameRecords)
            writer.writeString(record.getStringData());

        return writer.toByteArray();
    }

    private void calculateOffsets() throws IOException
    {
        int offset = getOffsetToStringStorage();
        int index = 0;
        for (NameRecord recordOn : nameRecords)
        {
            recordOn.setNameID(index);
            recordOn.setOffset(offset);
            log.debug("{} Name table sub table Offset Calc: ", offset);

            offset += recordOn.getLength();
            index ++;
        }
    }

    private int getOffsetToStringStorage()
    {
        return NAME_TABLE_HEADER_SIZE + (NAME_RECORD_SIZE * nameRecords.size());
    }

    @Override
    public String getName()
    {
        return "name";
    }

    protected static class NameRecord
    {
        static final int NAME_RECORD_SIZE = 12;

        @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
        int platformID = 1;

        @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
        int encodingID = 0;

        @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
        int languageID = 0;

        @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
        int nameID;

        @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
        int offset;

        public NameRecord(String name)
        {
            stringData = name;
        }

        @OpenTypeProperty(dataType = OpenTypeProperty.DataType.USHORT)
        private int getLength()
        {
            return getStringData().getBytes(openTypeCharEncoding).length;
        }

        public int getOffset()
        {
            return offset;
        }

        public void setOffset(int offset)
        {
            this.offset = offset;
        }

        public int getNameID()
        {
            return nameID;
        }

        public void setNameID(int nameID)
        {
            this.nameID = nameID;
        }

        private String stringData;

        public String getStringData()
        {
            return stringData;
        }

        public void setStringData(String stringData)
        {
            this.stringData = stringData;
        }

        public byte[] getRecordData() throws IOException
        {
            OpenTypeTableSerializer serializer = new OpenTypeTableSerializer();
            return serializer.serialize(this);
        }
    }
}
