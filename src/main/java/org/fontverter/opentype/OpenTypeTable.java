package org.fontverter.opentype;

import org.apache.commons.lang3.ArrayUtils;
import org.fontverter.FontAdapter;
import org.fontverter.FontVerterUtils;
import org.fontverter.io.*;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.fontverter.io.DataTypeProperty.*;
import static org.fontverter.io.DataTypeProperty.DataType.*;

public abstract class OpenTypeTable {
    public static final int TABLE_RECORD_SIZE = 16;
    private static List<Class> tableTypes;
    private static final Object factoryLock = new Object();

    public OtfTableRecord record;
    private long checksum;
    private int offset;
    private int paddingAdded;

    public OpenTypeTable() {
    }

    public static OpenTypeTable createFromRecord(OtfTableRecord record)
            throws IllegalAccessException, InstantiationException, IOException {
        initFactoryTableTypes();

        for (Class typeOn : tableTypes) {
            OpenTypeTable table = (OpenTypeTable) typeOn.newInstance();
            table.record = record;

            if (table.getName().equals(record.recordName))
                return table;
        }

        throw new IOException(String.format("OTF table type '%s' not implemented", record.recordName));
    }

    private static void initFactoryTableTypes() {
        synchronized (factoryLock) {
            if (tableTypes == null) {
                Reflections reflections = new Reflections("org.fontverter");
                Set<Class<? extends OpenTypeTable>> adapterClasses = reflections.getSubTypesOf(OpenTypeTable.class);
                tableTypes = Arrays.asList(adapterClasses.toArray(new Class[adapterClasses.size()]));

                List<Class> filteredTables = new ArrayList<Class>();
                for (Class tableTypeOn : tableTypes) {
                    if (!tableTypeOn.getCanonicalName().contains("Canned"))
                        filteredTables.add(tableTypeOn);
                }

                tableTypes = filteredTables;
            }
        }
    }

    public abstract String getName();

    public final byte[] getData() throws IOException {
        // open type tables should be padded to be divisible by 4
        return padTableData(getUnpaddedData());
    }

    public byte[] getUnpaddedData() throws IOException {
        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        return serializer.serialize(this);
    }

    public void readData(byte[] data) throws DataTypeSerializerException {
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        deserializer.deserialize(data, this);
    }

    public byte[] getRecordData() throws IOException {
        byte[] data = getData();

        OtfTableRecord record = new OtfTableRecord();
        record.recordName = getName();
        record.length = data.length - paddingAdded;
        record.checksum = (int) checksum;
        record.offset = getOffset();

        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        return serializer.serialize(record);
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

    public long getChecksum() {
        return checksum;
    }

    public static class OtfTableRecord {

        @DataTypeProperty(dataType = STRING, byteLength = 4)
        public String recordName;

        @DataTypeProperty(dataType = UINT)
        public long checksum;

        @DataTypeProperty(dataType = DataType.UINT)
        public long offset;

        @DataTypeProperty(dataType = DataType.UINT)
        public long length;
    }
}
