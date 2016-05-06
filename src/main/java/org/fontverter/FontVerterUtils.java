package org.fontverter;

import org.apache.fontbox.cff.CFFCharset;
import org.fontverter.io.ByteDataOutputStream;

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
        String dataAsString = new String(data, ByteDataOutputStream.openTypeCharset);
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
}
