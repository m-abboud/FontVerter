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

import org.mabb.fontverter.io.DataTypeBindingSerializer;
import org.mabb.fontverter.io.DataTypeProperty;

import java.io.IOException;

public class SfntHeader {
    public static final int SFNT_HEADER_SIZE = 12;
    public static final String CFF_FLAVOR = "OTTO";
    public static final String VERSION_1 = "\u0000\u0001\u0000\u0000";
    public static final String VERSION_2 = "\u0000\u0002\u0000\u0000";
    public static final String VERSION_2_5 = "\u0000\u0002\u0005\u0000";

    @DataTypeProperty(dataType = DataTypeProperty.DataType.STRING, constLength = 4)
    public String sfntFlavor = "";

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    public int numTables;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    public int searchRange;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    public int entrySelector;

    @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
    public int rangeShift;

    public void setNumTables(int numTables) {
        this.numTables = numTables;
        searchRange = closestMaxPowerOfTwo(numTables) * 16;
        rangeShift = numTables * 16 - searchRange;
        entrySelector = (int) log2(closestMaxPowerOfTwo(numTables));
    }

    private int closestMaxPowerOfTwo(double number) {
        int powerOfTwo = 1;
        while (powerOfTwo * 2 < number)
            powerOfTwo = powerOfTwo * 2;

        return powerOfTwo;
    }

    private double log2(int number) {
        return Math.log(number) / Math.log(2);
    }

    byte[] getData() throws IOException {
        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        return serializer.serialize(this);
    }

    float openTypeVersion() {
        // string version consts are kludge for getting around data type version difference string vs fixed
        // so don't have to write extra data type annotation logic.
        if (sfntFlavor.equals(CFF_FLAVOR))
            return 3;
        if (sfntFlavor.equals(VERSION_2))
            return 2;
        if (sfntFlavor.equals(VERSION_2_5))
            return 2.5F;

        return 1;
    }

    static String toVersionString(float i) {
        return i == 2.5 ? VERSION_2_5 : (i == 2 ? VERSION_2 : VERSION_1);
    }
}
