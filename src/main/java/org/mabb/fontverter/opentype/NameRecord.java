/*
 * Copyright (C) Maddie Abboud 2016
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

import org.mabb.fontverter.io.DataTypeSerializerException;
import org.mabb.fontverter.io.DataTypeProperty;
import org.mabb.fontverter.io.DataTypeBindingSerializer;

import java.nio.charset.Charset;

class NameRecord {
    static final int NAME_RECORD_SIZE = 12;

    public static NameRecord createWindowsRecord(String name, OtfNameConstants.RecordType type, OtfNameConstants.Language language) {
        NameRecord record = new NameRecord(name);
        record.setNameID(type.getValue());
        record.platformID = OtfNameConstants.WINDOWS_PLATFORM_ID;
        record.encodingID = OtfNameConstants.WINDOWS_DEFAULT_ENCODING;
        record.languageID = language.getValue();

        return record;
    }

    public static NameRecord createMacRecord(String name, OtfNameConstants.RecordType type, OtfNameConstants.Language language) {
        NameRecord record = new NameRecord(name);
        record.setNameID(type.getValue());
        record.platformID = OtfNameConstants.MAC_PLATFORM_ID;
        record.encodingID = OtfNameConstants.MAC_DEFAULT_ENCODING;
        record.languageID = 0;

        return record;
    }

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, order = 0)
    int platformID;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, order = 1)
    int encodingID;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, order = 2)
    int languageID;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, order = 3)
    int nameID;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, order = 4)
    int getLength() {
        return getStringData().length;
    }

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT, order = 5)
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
        if (platformID == OtfNameConstants.WINDOWS_PLATFORM_ID)
            return Charset.forName("UTF-16");
        return Charset.forName("ISO_8859_1");
    }

    public void setStringData(String stringData) {
        this.string = stringData;
    }

    public byte[] getRecordData() throws DataTypeSerializerException {
        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        return serializer.serialize(this);
    }
}
