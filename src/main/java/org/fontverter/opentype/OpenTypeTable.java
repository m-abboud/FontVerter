package org.fontverter.opentype;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class OpenTypeTable
{
    public static final int TABLE_RECORD_SIZE = 16;

    private long checksum;
    private int offset;

    private int paddingAdded;

    public OpenTypeTable()
    {
    }

    public final byte[] getData() throws IOException, FontSerializerException {
        // open type tables should be padded to be divisible by 4
        return padTableData(getRawData());
    }

    protected byte[] getRawData() throws IOException, FontSerializerException {
        OtfTableSerializer serializer = new OtfTableSerializer();
        return serializer.serialize(this);
    }

    public abstract String getName();

    public byte[] getRecordEntry() throws IOException, FontSerializerException {
        OtfWriter writer = new OtfWriter();
        byte[] data = getData();

        writer.writeString(getName());
        writer.writeUnsignedInt((int) checksum);
        writer.writeUnsignedInt(getOffset());
        writer.writeUnsignedInt(data.length - paddingAdded);

        return writer.toByteArray();
    }

    private byte[] padTableData(byte[] tableData) {
        if (tableData.length % 4 != 0) {
            int paddingNeeded = 4- (tableData.length % 4);

            byte[] padding = new byte[paddingNeeded];
            for (int i = 0; i < padding.length; i++)
                padding[i] = 0;

            paddingAdded = paddingNeeded;
            return ArrayUtils.addAll(tableData, padding);
        }

        paddingAdded = 0;
        return tableData;
    }

    public void finalizeRecord() throws IOException, FontSerializerException {
        checksum = getTableChecksum(getData());
    }

    void normalize() {
    }

    protected final long getTableChecksum(byte[] tableData) throws IOException
    {
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(tableData));

        long checksum = 0;
        while (is.available() >= 4)
            checksum = checksum + is.readInt();

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
