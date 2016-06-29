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

package org.mabb.fontverter;

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.FontDataOutputStream;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FontVerterUtils {
    public static Field findPrivateField(String fieldName, Class type) {
        Field[] fields = type.getDeclaredFields();

        Field mapField = null;
        for (Field fieldOn : fields) {
            if (fieldOn.getName().equals(fieldName)) {
                mapField = fieldOn;
                mapField.setAccessible(true);
            }
        }
        return mapField;
    }

    public static Method findPrivateMethod(String methodName, Class type) {
        Method[] methods = type.getDeclaredMethods();

        Method mapMethod = null;
        for (Method methodOn : methods) {
            if (methodOn.getName().contains(methodName)) {
                mapMethod = methodOn;
                mapMethod.setAccessible(true);
            }
        }

        return mapMethod;
    }

    public static boolean bytesStartsWith(byte[] data, String... startsWith) {
        String dataAsString = new String(data, FontDataOutputStream.OPEN_TYPE_CHARSET);
        for (String matchOn : startsWith)
            if (dataAsString.startsWith(matchOn))
                return true;

        return false;
    }

    public static long getTableChecksum(byte[] tableData) throws IOException {
        FontDataInputStream is = new FontDataInputStream(tableData);

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

    public static int readUpperBits(int inByte, int nBits) throws IOException {
        if (nBits > 8)
            throw new IOException("Number of bits exceeds 8");

        return inByte >> (8 - nBits);
    }

    public static int readLowerBits(int inByte, int nBits) throws IOException {
        if (nBits > 8)
            throw new IOException("Number of bits exceeds 8");

        return inByte & (0x1F);
    }

    public static byte[] toPrimative(Byte[] objArray) {
        byte[] primArr = new byte[objArray.length];
        for (int i = 0; i < objArray.length; i++)
            primArr[i] = objArray[i];

        return primArr;
    }
}
