package org.fontverter.opentype;

import java.nio.charset.Charset;

import static org.fontverter.opentype.OtfSerializerProperty.*;

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

    @OtfSerializerProperty(dataType = DataType.USHORT, order = 0)
    int platformID;

    @OtfSerializerProperty(dataType = DataType.USHORT, order = 1)
    int encodingID;

    @OtfSerializerProperty(dataType = DataType.USHORT, order = 2)
    int languageID;

    @OtfSerializerProperty(dataType = DataType.USHORT, order = 3)
    int nameID;

    @OtfSerializerProperty(dataType = DataType.USHORT, order = 4)
    int getLength() {
        return getStringData().length;
    }

    @OtfSerializerProperty(dataType = DataType.USHORT, order = 5)
    int offset;

    private NameRecord(String name) {
        stringData = name;
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

    private String stringData;

    public byte[] getStringData() {
        return stringData.getBytes(getEncoding());
    }

    private Charset getEncoding() {
        if(platformID == OtfNameConstants.WINDOWS_PLATFORM_ID)
            return Charset.forName("UTF-16");
        return Charset.forName("ISO_8859_1");
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    public byte[] getRecordData() throws FontSerializerException {
        OtfTableSerializer serializer = new OtfTableSerializer();
        return serializer.serialize(this);
    }
}
