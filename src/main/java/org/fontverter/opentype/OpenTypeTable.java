package org.fontverter.opentype;

import org.fontverter.FontWriter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class OpenTypeTable
{
    public static final int TABLE_RECORD_SIZE = 16;

    private int offset;

    public OpenTypeTable()
    {
    }

    public byte[] getData() throws IOException
    {
        OpenTypeTableSerializer serializer = new OpenTypeTableSerializer();
        return serializer.serialize(this);
    }

    public abstract String getName();

    public byte[] getRecordEntry() throws IOException
    {
        FontWriter writer = FontWriter.createWriter();
        long checksum = calcTableChecksum(getData());

        writer.writeString(getName());
        writer.writeUnsignedInt((int) checksum);
        writer.writeUnsignedInt(getOffset());
        writer.writeUnsignedInt((getData().length));

        return writer.toByteArray();
    }

    long calcTableChecksum(byte[] tableData) throws IOException
    {
        long checksum = 0;
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(tableData));
        while (is.available() > 4)
            checksum = checksum + is.readLong();
        is.close();
        return checksum;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

}
