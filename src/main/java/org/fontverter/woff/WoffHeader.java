package org.fontverter.woff;

import org.fontverter.io.ByteBindingSerializer;
import org.fontverter.io.ByteDataProperty;
import org.fontverter.io.ByteSerializerException;

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
        ByteBindingSerializer serializer = new ByteBindingSerializer();
        return serializer.serialize(this);
    }

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int signature = WOFF_2_SIGNATURE;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int flavorSfntVersion;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int length;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    short numTables;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    static final short reserved = 0;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int totalSfntSize;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int totalCompressedSize;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    short majorVersion;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.SHORT)
    short minorVersion;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int metaOffset;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int metaLength;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int metaOrigLength;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int privOffset;

    @ByteDataProperty(dataType = ByteDataProperty.DataType.INT)
    int privLength;

    public void calculateValues(WoffFont woffFont) throws IOException {
        length = woffFont.getRawData().length;
        numTables = (short) woffFont.getTables().size();
        totalCompressedSize = woffFont.getCompressedDataBlock().length;
        totalSfntSize = woffFont.getFonts().get(0).getData().length;
        flavorSfntVersion = 0x4F54544F;// "OTTO".he;
    }
}

