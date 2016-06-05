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

package org.mabb.fontverter.woff;

import org.mabb.fontverter.io.DataTypeBindingSerializer;
import org.mabb.fontverter.io.DataTypeProperty;

import java.io.IOException;

public class WoffHeader {
    static final int WOFF_1_SIGNATURE = 0x774F4646;
    static final int WOFF_2_SIGNATURE = 0x774F4632;

    public static WoffHeader createWoff2Header() {
        WoffHeader header = new WoffHeader();
        header.signature = WOFF_2_SIGNATURE;
        header.majorVersion = 2;
        header.minorVersion = 0;
        return header;
    }

    public static WoffHeader createWoff1Header() {
        WoffHeader header = new WoffHeader();
        header.signature = WOFF_1_SIGNATURE;
        header.majorVersion = 1;
        header.minorVersion = 0;
        return header;
    }

    public byte[] getData() throws IOException {
        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        return serializer.serialize(this);
    }

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int signature = WOFF_2_SIGNATURE;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int flavorSfntVersion;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int length;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    short numTables;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    short reserved = 0;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int totalSfntSize;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT, ignoreIf = "isVersionOne")
    int totalCompressedSize;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    short majorVersion;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.SHORT)
    short minorVersion;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int metaOffset = 0;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int metaLength = 0;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int metaOrigLength = 0;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int privOffset = 0;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.INT)
    int privLength = 0;

    public void calculateValues(WoffFont woffFont) throws IOException {
        length = woffFont.getRawData().length;
        numTables = (short) woffFont.getTables().size();

        totalCompressedSize = woffFont.getCompressedSize();
        totalSfntSize = woffFont.getFonts().get(0).getData().length;
        flavorSfntVersion = 0x4F54544F;
    }

    public boolean isVersionOne() {
        clean();
        return majorVersion == 1;
    }


    // !! fixme waiiit I think majorVersion might be the font version not woff version?
    private void clean() {
        majorVersion = getWoffSignatureVersion();
    }

    short getWoffSignatureVersion() {
        if (signature == WOFF_1_SIGNATURE)
            return 1;
        else if (signature == WOFF_2_SIGNATURE)
            return 2;

        return -1;
    }

    public boolean isSignatureValid() {
        return signature == WOFF_2_SIGNATURE || signature == WOFF_1_SIGNATURE;
    }
}

