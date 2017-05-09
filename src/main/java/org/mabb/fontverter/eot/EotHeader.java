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

package org.mabb.fontverter.eot;

import com.google.common.primitives.Bytes;
import org.mabb.fontverter.io.DataTypeBindingSerializer;
import org.mabb.fontverter.io.DataTypeProperty;
import org.mabb.fontverter.io.DataTypeSerializerException;
import org.mabb.fontverter.io.LittleEndianOutputStream;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.mabb.fontverter.io.DataTypeProperty.DataType.*;

public class EotHeader {
    public final static long VERSION_ONE = 0x00010000;
    public final static long VERSION_TWO = 0x00020001;
    public final static long VERSION_THREE = 0x00020002;

    @DataTypeProperty(dataType = ULONG)
    long eotSize = 0;

    @DataTypeProperty(dataType = ULONG)
    long fontDataSize = 0;

    @DataTypeProperty(dataType = ULONG)
    long version = VERSION_TWO;

    @DataTypeProperty(dataType = ULONG)
    long flags = 0;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.BYTE_ARRAY, constLength = 10)
    byte[] panose = new byte[10];

    @DataTypeProperty(dataType = DataTypeProperty.DataType.BYTE)
    byte charset;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.BYTE)
    byte italic;

    @DataTypeProperty(dataType = ULONG)
    long weight = 0;

    @DataTypeProperty(dataType = USHORT)
    int fsType = 0;

    @DataTypeProperty(dataType = USHORT)
    int magicNumber = 0x504C;

    @DataTypeProperty(dataType = ULONG)
    long unicodeRange1;

    @DataTypeProperty(dataType = ULONG)
    long unicodeRange2;

    @DataTypeProperty(dataType = ULONG)
    long unicodeRange3;

    @DataTypeProperty(dataType = ULONG)
    long unicodeRange4;

    @DataTypeProperty(dataType = ULONG)
    long codePageRange1;

    @DataTypeProperty(dataType = ULONG)
    long codePageRange2;

    @DataTypeProperty(dataType = ULONG)
    long checkSumAdjustment;

    @DataTypeProperty(dataType = ULONG)
    long reserved1 = 0;

    @DataTypeProperty(dataType = ULONG)
    long reserved2 = 0;

    @DataTypeProperty(dataType = ULONG)
    long reserved3 = 0;

    @DataTypeProperty(dataType = ULONG)
    long reserved4 = 0;

    @DataTypeProperty(dataType = USHORT)
    int padding1 = 0;

    @DataTypeProperty(dataType = USHORT)
    int familyNameSize = 0;

    @DataTypeProperty(dataType = BYTE, isArray = true, arrayLength = "familyNameSize")
    Byte[] familyName = new Byte[0];

    @DataTypeProperty(dataType = USHORT)
    int padding2 = 0;

    @DataTypeProperty(dataType = USHORT)
    int styleNameSize;

    @DataTypeProperty(dataType = BYTE, isArray = true, arrayLength = "styleNameSize")
    Byte[] styleName = new Byte[0];

    @DataTypeProperty(dataType = USHORT)
    int Padding3 = 0;

    @DataTypeProperty(dataType = USHORT)
    int versionNameSize;

    @DataTypeProperty(dataType = BYTE, isArray = true, arrayLength = "versionNameSize")
    Byte[] versionName = new Byte[0];

    @DataTypeProperty(dataType = USHORT)
    int Padding4 = 0;

    @DataTypeProperty(dataType = USHORT)
    int fullNameSize = 0;

    @DataTypeProperty(dataType = BYTE, isArray = true, arrayLength = "fullNameSize")
    Byte[] fullName = new Byte[0];

    @DataTypeProperty(dataType = USHORT)
    int padding5 = 0;
    ;

    @DataTypeProperty(dataType = USHORT)
    int rootStringSize = 0;

    @DataTypeProperty(dataType = BYTE, isArray = true, arrayLength = "rootStringSize")
    Byte[] rootString = new Byte[0];

    public String getFamilyName() {
        byte[] family = Bytes.toArray(Arrays.asList(familyName));
        return new String(family);
    }

    public String getRootString() {
        byte[] root = Bytes.toArray(Arrays.asList(rootString));
        return new String(root);
    }

    public String getFullNameString() {
        byte[] fullName = Bytes.toArray(Arrays.asList(this.fullName));
        try {
            return new String(fullName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public byte[] getData() throws DataTypeSerializerException {
        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        return serializer.serialize(this, new LittleEndianOutputStream());
    }

    public boolean isValid() {
        boolean versionMatches = version == VERSION_ONE || version == VERSION_TWO
                || version == VERSION_THREE;

        return magicNumber == 0x504C && versionMatches;

    }
}
