package org.fontverter.opentype;

import org.fontverter.FontWriter;
import org.fontverter.opentype.OtfNameConstants.RecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.fontverter.opentype.NameRecord.NAME_RECORD_SIZE;

public class NameTable extends OpenTypeTable {
    static final int NAME_TABLE_HEADER_SIZE = 6;
    private static Logger log = LoggerFactory.getLogger(NameTable.class);

    private List<NameRecord> nameRecords = new ArrayList<NameRecord>();
    private int formatSelector = 0;
    private OtfNameConstants.Language defaultLanguage = OtfNameConstants.Language.UNITED_STATES;

    public static NameTable createDefaultTable() {
        NameTable table = new NameTable();

        table.setCopyright("Default Copyright");
        table.setFontFamily("DefaultFamily");
        table.setFontSubFamily("DefaultSubFamily");
        table.setVersion("Version 1.1");
        table.setUniqueId(UUID.randomUUID().toString().replace("-", ""));
        table.setFontFullName("DefaultFontFullName");
        table.setPostScriptName("DefaultPostScriptName");

        return table;
    }

    public void setFontFamily(String family) {
        addName(family, OtfNameConstants.RecordType.FONT_FAMILY, defaultLanguage);
    }

    public void setCopyright(String name) {
        addName(name, OtfNameConstants.RecordType.COPYRIGHT, defaultLanguage);
    }

    public void setFontSubFamily(String name) {
        addName(name, OtfNameConstants.RecordType.FONT_SUB_FAMILY, defaultLanguage);
    }

    public void setFontFullName(String name) {
        addName(name, OtfNameConstants.RecordType.FULL_FONT_NAME, defaultLanguage);
    }

    public void setUniqueId(String name) {
        addName(name, OtfNameConstants.RecordType.UNIQUE_FONT_ID, defaultLanguage);
    }

    public void setPostScriptName(String name) {
        addName(name, OtfNameConstants.RecordType.POSTSCRIPT_NAME, defaultLanguage);
    }

    public void setVersion(String name) {
        addName(name, OtfNameConstants.RecordType.VERSION_STRING, defaultLanguage);
    }

    public String getName(RecordType type) {
        for(NameRecord recordOn : nameRecords)
            if(recordOn.nameID == type.getValue())
                return recordOn.getRawString();

        return null;
    }

    private void addName(String name, RecordType type, OtfNameConstants.Language language) {
        deleteExisting(type, language);
        NameRecord windowsRecord = NameRecord.createWindowsRecord(name, type, language);
        nameRecords.add(windowsRecord);

        NameRecord macRecord = NameRecord.createMacRecord(name, type, language);
        nameRecords.add(macRecord);
    }

    private void deleteExisting(RecordType type, OtfNameConstants.Language language) {
        List<NameRecord> deleteList = new LinkedList<NameRecord>();
        for (NameRecord recordOn : nameRecords)
            //recordOn.languageID == language.getValue() &&
            if (recordOn.nameID == type.getValue())
                deleteList.add(recordOn);

        for (NameRecord recordOn : deleteList)
            nameRecords.remove(recordOn);
    }

    protected byte[] getRawData() throws IOException, FontSerializerException {
        FontWriter writer = FontWriter.createWriter();
        writer.writeUnsignedShort(formatSelector);
        writer.writeUnsignedShort(nameRecords.size());
        writer.writeUnsignedShort(getOffsetToStringStorage());

        calculateOffsets();

        Collections.sort(nameRecords, new Comparator<NameRecord>() {
            @Override
            public int compare(NameRecord o1, NameRecord o2) {
                if (o1.platformID != o2.platformID)
                    return o1.platformID < o2.platformID ? -1 : 1;
                return o1.nameID < o2.nameID ? -1 : o1.nameID == o2.nameID ? 0 : 1;

            }
        });

        for (NameRecord record : nameRecords)
            writer.write(record.getRecordData());

        for (NameRecord record : nameRecords)
            writer.write(record.getStringData());

        return writer.toByteArray();
    }

    private void calculateOffsets() throws IOException {
        int offset = 0;
        for (NameRecord recordOn : nameRecords) {
            recordOn.setOffset(offset);
            log.debug("{} Name table sub table Offset Calc: ", offset);

            offset += recordOn.getLength();
        }
    }

    private int getOffsetToStringStorage() {
        return NAME_TABLE_HEADER_SIZE + (NAME_RECORD_SIZE * nameRecords.size());
    }

    @Override
    public String getName() {
        return "name";
    }

}
