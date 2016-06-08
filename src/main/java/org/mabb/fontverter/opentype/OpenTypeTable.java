/*
 * Copyright (C) Matthew Abboud 2016
 *
 * FontVerter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FontVerter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FontVerter. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mabb.fontverter.opentype;

import org.apache.commons.lang3.ArrayUtils;
import org.mabb.fontverter.FontVerterUtils;
import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.DataTypeBindingSerializer;
import org.mabb.fontverter.io.DataTypeProperty;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class OpenTypeTable {
    public static class OtfTableRecord {
        @DataTypeProperty(dataType = DataTypeProperty.DataType.STRING, constLength = 4)
        public String recordName;

        @DataTypeProperty(dataType = DataTypeProperty.DataType.UINT)
        public long checksum;

        @DataTypeProperty(dataType = DataTypeProperty.DataType.UINT)
        public long offset;

        @DataTypeProperty(dataType = DataTypeProperty.DataType.UINT)
        public long length;

        public byte[] originalData;
    }

    public static final int TABLE_RECORD_SIZE = 16;

    private static List<Class> tableTypes;
    private static final Object factoryLock = new Object();
    private static Logger log = LoggerFactory.getLogger(OpenTypeTable.class);

    public OtfTableRecord record;
    public boolean isFromParsedFont = false;
    protected OpenTypeFont font;
    protected byte[] cachedUnpaddedData;
    private long checksum;
    private long offset;
    private int paddingAdded;

    // big old kludge to handle conversion of tables types that arn't deserializable/parsable yet
    private byte[] rawParsedData;

    public OpenTypeTable() {
    }

    public static OpenTypeTable createFromRecord(OtfTableRecord record, OpenTypeFont font)
            throws IllegalAccessException, InstantiationException, IOException {
        initFactoryTableTypes();

        OpenTypeTable createdTable = null;
        for (Class typeOn : tableTypes) {
            OpenTypeTable tableOn = (OpenTypeTable) typeOn.newInstance();

            if (tableOn.getTableType().equals(record.recordName)) {
                createdTable = tableOn;
                break;
            }
        }

        if (createdTable == null)
            createdTable = createUnknownTableTypeFallback(record);

        createdTable.record = record;
        createdTable.font = font;
        createdTable.offset = record.offset;

        return createdTable;
    }

    private static OpenTypeTable createUnknownTableTypeFallback(OtfTableRecord record) {
        log.debug(String.format("OTF table type '%s' not implemented, using fallback blind table parsing.",
                record.recordName));

        OpenTypeTable table = new UnknownTableType(record.recordName);
        table.record = record;

        return table;
    }

    private static void initFactoryTableTypes() {
        synchronized (factoryLock) {
            if (tableTypes == null) {
                Reflections reflections = new Reflections("org.mabb.fontverter");
                Set<Class<? extends OpenTypeTable>> adapterClasses = reflections.getSubTypesOf(OpenTypeTable.class);
                tableTypes = Arrays.asList(adapterClasses.toArray(new Class[adapterClasses.size()]));

                List<Class> filteredTables = new ArrayList<Class>();
                for (Class tableTypeOn : tableTypes) {
                    if (!tableTypeOn.getCanonicalName().contains("Canned") && tableTypeOn != UnknownTableType.class)
                        filteredTables.add(tableTypeOn);
                }

                tableTypes = filteredTables;
            }
        }
    }

    /* overly descriptive method name to avoid confusion with other getName methods */
    public abstract String getTableType();

    public final byte[] getData() throws IOException {
        // open type tables should be padded to be divisible by 4
        return padTableData(getUnpaddedData());
    }

    public final byte[] getUnpaddedData() throws IOException {
        // big old kludge to handle conversion of tables types that arn't deserializable/parsable yet
        if (rawParsedData != null)
            return rawParsedData;

        if (cachedUnpaddedData != null)
            return cachedUnpaddedData;

        // open type tables should be padded to be divisible by 4
        cachedUnpaddedData = generateUnpaddedData();
        return cachedUnpaddedData;
    }

    protected byte[] generateUnpaddedData() throws IOException {
        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        return serializer.serialize(this);
    }

    public void readData(byte[] data) throws IOException {
        if (!isParsingImplemented()) {
            rawParsedData = data;
            return;
        }

        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        deserializer.deserialize(data, this);
    }

    /* big old kludge to handle conversion of tables types that arn't deserializable/parsable yet remove asap*/
    protected boolean isParsingImplemented() {
        return true;
    }

    public byte[] getRecordData() throws IOException {
        byte[] data = getData();

        OtfTableRecord record = new OtfTableRecord();
        record.recordName = getTableType();
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

    void normalize() throws IOException {
    }

    public long getOffset() {
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

    /**
     * Should be called before/after font data generation. While building up the font generateData is called multiple
     * times to calculate offsets and checksums before writing out the full font.
     */
    public void clearDataCache() {
        cachedUnpaddedData = null;
    }
}
