package org.fontverter.opentype;

import java.nio.charset.Charset;

import static org.fontverter.opentype.OtfDataProperty.*;

class NameRecord {
    static final int NAME_RECORD_SIZE = 12;

    public static NameRecord createWindowsRecord(String name, OtfNameConstants.RecordType type, OtfNameConstants.Language language) {
        NameRecord record = new NameRecord(name);
        record.setNameID(type.getValue());
        record.platformID = OtfNameConstants.WINDOWS_PLATFORM_ID;
        record.encodingID = OtfNameConstants.WINDOWS_ENCODING;
        record.languageID = language.getValue();

        return record;
    }

    public static NameRecord createMacRecord(String name, OtfNameConstants.RecordType type, OtfNameConstants.Language language) {
        NameRecord record = new NameRecord(name);
        record.setNameID(type.getValue());
        record.platformID = OtfNameConstants.MAC_PLATFORM_ID;
        record.encodingID = OtfNameConstants.MAC_ENCODING;
        record.languageID = 0;

        return record;
    }

    @OtfDataProperty(dataType = DataType.USHORT, order = 0)
    int platformID;

    @OtfDataProperty(dataType = DataType.USHORT, order = 1)
    int encodingID;

    @OtfDataProperty(dataType = DataType.USHORT, order = 2)
    int languageID;

    @OtfDataProperty(dataType = DataType.USHORT, order = 3)
    int nameID;

    @OtfDataProperty(dataType = DataType.USHORT, order = 4)
    int getLength() {
        return getStringData().length;
    }

    @OtfDataProperty(dataType = DataType.USHORT, order = 5)
    int offset;

    private NameRecord(String name) {
        string = name;
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getNameID() {
        return nameID;
    }

    public void setNameID(int nameID) {
        this.nameID = nameID;
    }

    private String string;

    public byte[] getStringData() {
        return string.getBytes(getEncoding());
    }

    public String getRawString() {
        return string;
    }

    private Charset getEncoding() {
        if(platformID == OtfNameConstants.WINDOWS_PLATFORM_ID)
            return Charset.forName("UTF-16");
        return Charset.forName("ISO_8859_1");
    }

    public void setStringData(String stringData) {
        this.string = stringData;
    }

    public byte[] getRecordData() throws FontSerializerException {
        OtfTableSerializer serializer = new OtfTableSerializer();
        return serializer.serialize(this);
    }
}
