package org.fontverter.woff;

import org.fontverter.io.DataTypeBindingSerializer;
import org.fontverter.io.DataTypeProperty;

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

