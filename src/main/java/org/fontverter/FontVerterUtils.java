package org.fontverter;

import org.fontverter.io.ByteDataInputStream;
import org.fontverter.io.ByteDataOutputStream;

import java.io.IOException;
import java.lang.reflect.Field;

public class FontVerterUtils {
    public static Field findPrivateField(String fieldName, Class type) {
        Field[] fields = type.getDeclaredFields();

        Field mapField = null;
        for (Field fieldOn : fields) {
            if (fieldOn.getName().contains(fieldName)) {
                mapField = fieldOn;
                mapField.setAccessible(true);
            }
        }
        return mapField;
    }

    public static boolean bytesStartsWith(byte[] data, String... startsWith) {
        String dataAsString = new String(data, ByteDataOutputStream.OPEN_TYPE_CHARSET);
        for (String matchOn : startsWith)
            if (dataAsString.startsWith(matchOn))
                return true;

        return false;
    }

    public static boolean bytesStartsWith(byte[] data, byte[] startsWith) {
        if (data.length < startsWith.length)
            return false;

        for (int i = 0; i < startsWith.length; i++) {
            if (data[i] != startsWith[i])
                return false;
        }

        return true;
    }

    public static long getTableChecksum(byte[] tableData) throws IOException {
        ByteDataInputStream is = new ByteDataInputStream(tableData);

        long checksum = 0;
        while (is.available() >= 4)
            checksum = checksum + is.readUnsignedInt();

        is.close();
        return checksum;
    }

    public static byte[] tablePaddingNeeded(byte[] tableData) {
        if (tableData.length % 4 == 0)
            return new byte[]{};

        int paddingNeeded = 4 - (tableData.length % 4);
        byte[] padding = new byte[paddingNeeded];
        for (int i = 0; i < padding.length; i++)
            padding[i] = '\u0000';

        return padding;
    }
}
