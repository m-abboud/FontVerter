package org.fontverter;

import org.apache.fontbox.cff.CFFCharset;

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

}
