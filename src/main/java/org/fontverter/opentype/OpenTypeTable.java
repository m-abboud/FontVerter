package org.fontverter.opentype;

import org.apache.commons.lang3.ArrayUtils;
import org.fontverter.FontVerterUtils;
import org.fontverter.io.ByteDataOutputStream;
import org.fontverter.io.ByteBindingSerializer;

import java.io.IOException;

public abstract class OpenTypeTable {
    public static final int TABLE_RECORD_SIZE = 16;

    public long getChecksum() {
        return checksum;
    }

    private long checksum;
    private int offset;

    private int paddingAdded;

    public OpenTypeTable() {
    }

    public final byte[] getData() throws IOException {
        // open type tables should be padded to be divisible by 4
        return padTableData(getUnpaddedData());
    }

    public byte[] getUnpaddedData() throws IOException {
        ByteBindingSerializer serializer = new ByteBindingSerializer();
        return serializer.serialize(this);
    }


    public abstract String getName();

    public byte[] getRecordEntry() throws IOException {
        ByteDataOutputStream writer = new ByteDataOutputStream(ByteDataOutputStream.OPEN_TYPE_CHARSET);
        byte[] data = getData();

        writer.writeString(getName());
        writer.writeUnsignedInt((int) checksum);
        writer.writeUnsignedInt(getOffset());
        writer.writeUnsignedInt(data.length - paddingAdded);

        return writer.toByteArray();
    }

    private byte[] padTableData(byte[] tableData) {
        byte[] padding = FontVerterUtils.tablePaddingNeeded(tableData);
        paddingAdded = padding.length;
        return ArrayUtils.addAll(tableData, padding);
    }

    public void finalizeRecord() throws IOException {
        checksum = FontVerterUtils.getTableChecksum(getData());
    }

    void normalize() {
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    void resetCalculations() {
        checksum = 0;
        offset = 0;
    }
}
