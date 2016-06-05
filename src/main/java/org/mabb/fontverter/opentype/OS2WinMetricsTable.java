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

package org.mabb.fontverter.opentype;

import org.apache.commons.lang3.ArrayUtils;
import org.mabb.fontverter.io.DataTypeProperty;

import java.util.ArrayList;
import java.util.Collections;

import static org.mabb.fontverter.io.DataTypeProperty.*;
import static org.mabb.fontverter.opentype.OtfNameConstants.*;

public class OS2WinMetricsTable extends OpenTypeTable {
    @DataTypeProperty(dataType = DataType.USHORT)
    private int version;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short averageCharWidth;

    @DataTypeProperty(dataType = DataType.USHORT)
    private int weightClass;

    @DataTypeProperty(dataType = DataType.USHORT)
    private int widthClass;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short fsType;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short subscriptXSize;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short subscriptYSize;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short subscriptXOffset;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short subscriptYOffset;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short superscriptXSize;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short superscriptYSize;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short superscriptXOffset;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short superscriptYOffset;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short strikeoutSize;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short strikeoutPosition;

    @DataTypeProperty(dataType = DataType.USHORT)
    private int familyClass;

    @DataTypeProperty(dataType = DataType.BYTE_ARRAY, byteLength = 10)
    private byte[] panose = new byte[10];

    @DataTypeProperty(dataType = DataType.UINT)
    private long unicodeRange1;

    @DataTypeProperty(dataType = DataType.UINT)
    private long unicodeRange2;

    @DataTypeProperty(dataType = DataType.UINT)
    private long unicodeRange3;

    @DataTypeProperty(dataType = DataType.UINT)
    private long unicodeRange4;

    @DataTypeProperty(dataType = DataType.STRING, byteLength = 4)
    private String achVendId;

    @DataTypeProperty(dataType = DataType.USHORT)
    private int fsSelection;

    @DataTypeProperty(dataType = DataType.USHORT)
    private int firstCharIndex;

    @DataTypeProperty(dataType = DataType.USHORT)
    private int lastCharIndex;

    @DataTypeProperty(dataType = DataType.SHORT)
    private int typoAscender;

    @DataTypeProperty(dataType = DataType.SHORT)
    private int typoDescender;

    @DataTypeProperty(dataType = DataType.SHORT)
    private int typoLineGap;

    @DataTypeProperty(dataType = DataType.USHORT)
    private int winAscent;

    @DataTypeProperty(dataType = DataType.USHORT)
    private int winDescent;

    @DataTypeProperty(dataType = DataType.UINT, ignoreIf = "!isVersion1OrHigher")
    private long codePageRange1;

    @DataTypeProperty(dataType = DataType.UINT, ignoreIf = "!isVersion1OrHigher")
    private long codePageRange2;

    @DataTypeProperty(dataType = DataType.SHORT, ignoreIf = "!isVersion2OrHigher")
    private int sxHeight;

    @DataTypeProperty(dataType = DataType.SHORT, ignoreIf = "!isVersion2OrHigher")
    private int sCapHeight;

    @DataTypeProperty(dataType = DataType.USHORT, ignoreIf = "!isVersion2OrHigher")
    private int usDefaultChar;

    @DataTypeProperty(dataType = DataType.USHORT, ignoreIf = "!isVersion2OrHigher")
    private int usBreakChar;

    @DataTypeProperty(dataType = DataType.USHORT, ignoreIf = "!isVersion2OrHigher")
    private int usMaxContext;

    private UnicodeRanges unicodeRanges = new UnicodeRanges();
    private CodePageRanges codePageRanges = new CodePageRanges();
    private static byte[] latinPanose = new byte[]{2, 0, 6, 3, 0, 0, 0, 0, 0, 0};

    public OS2WinMetricsTable() {
    }

    public String getTableType() {
        return "OS/2";
    }

    public static OS2WinMetricsTable createDefaultTable() {
        OS2WinMetricsTable table = new OS2WinMetricsTable();
        table.version = 3;
        table.averageCharWidth = 1304;
        table.weightClass = 500;
        table.widthClass = 5;
        table.fsType = 0;
        table.subscriptXSize = 650;
        table.subscriptYSize = 699;
        table.subscriptXOffset = 0;
        table.subscriptYOffset = 140;
        table.superscriptXSize = 650;
        table.superscriptYSize = 699;
        table.superscriptXOffset = 0;
        table.superscriptYOffset = 479;
        table.strikeoutSize = 49;
        table.strikeoutPosition = 258;
        table.familyClass = 0;
        table.panose = latinPanose.clone();
        table.unicodeRange1 = 1;
        table.unicodeRange2 = 0;
        table.unicodeRange3 = 0;
        table.unicodeRange4 = 0;
        table.achVendId = "xxxx";
        table.fsSelection = 0;
        table.firstCharIndex = 59;
        table.lastCharIndex = 123;
        table.typoAscender = 800;
        table.typoDescender = -200;
        table.typoLineGap = 90;
        table.winAscent = 796;
        table.winDescent = 133;
        table.codePageRange1 = 0;
        table.codePageRange2 = 0;
        table.sxHeight = 0;
        table.sCapHeight = 769;
        table.usDefaultChar = 32;
        table.usBreakChar = 32;
        table.usMaxContext = 1;

        return table;
    }

    public boolean isVersion1OrHigher() {
        return version >= 1;
    }

    public boolean isVersion2OrHigher() {
        return version >= 2;
    }

    public short getAverageCharWidth() {
        return averageCharWidth;
    }

    void normalize() {
        super.normalize();
        if(panose == null)
            panose = latinPanose.clone();
        calcPanose();
        calcEncodingRanges();

        if (!isFromParsedFont) {
            winAscent = font.getHhea().ascender;
            winDescent = font.getHhea().descender;
        }
    }

    private void calcPanose() {
        OtfEncodingType encode = font.getCmap().getCmapEncodingType();
        if (encode == OtfEncodingType.SYMBOL) {
            panose[0] = 5;
            panose[2] = 1;
            panose[4] = 1;
        }
        else
            panose[0] = 2;
    }


    private void calcEncodingRanges() {
        if (isFromParsedFont)
            return;

        OtfEncodingType encode = font.getCmap().getCmapEncodingType();
        if (encode == OtfEncodingType.SYMBOL) {
            codePageRanges.setPageBit(CodePageRange.SYMBOL_CHARACTER_SET, true);
        } else {
            unicodeRanges.setPageBit(OtfUnicodeRange.BASIC_LATIN, true);
        }

        unicodeRange1 = unicodeRanges.getRanges().get(0);
        unicodeRange2 = unicodeRanges.getRanges().get(1);
        unicodeRange3 = unicodeRanges.getRanges().get(2);
        unicodeRange4 = unicodeRanges.getRanges().get(3);
        
        codePageRange1 = codePageRanges.getRanges().get(0);
        codePageRange2 = codePageRanges.getRanges().get(1);
    }

    static class BinaryBlock {
        protected boolean[] binary;

        BinaryBlock(int size) {
            binary = new boolean[size];
            for (int i = 0; i < binary.length; i++)
                binary[i] = false;
        }

        ArrayList<Long> getRanges() {
            ArrayList<Long> ranges = new ArrayList<Long>();

            boolean[] reversedBinary = binary.clone();
            ArrayUtils.reverse(reversedBinary);

            long n = 0;
            for (int i = 0; i < reversedBinary.length; i++) {
                boolean b = reversedBinary[i];
                n = (n << 1) | (b ? 1 : 0);

                boolean isLastBit = (i + 1) % 32 == 0;
                if (isLastBit) {
                    ranges.add(n);
                    n = 0;
                }
            }

            Collections.reverse(ranges);
            return ranges;
        }
    }

    private static class UnicodeRanges extends BinaryBlock {
        UnicodeRanges() {
            super(128);
        }

        public void setPageBit(OtfUnicodeRange range, boolean enable) {
            binary[range.bit] = enable;
        }

        public void setPageBit(OtfUnicodeRange range) {
            binary[range.bit] = true;
        }
    }

    private static class CodePageRanges extends BinaryBlock {
        CodePageRanges() {
            super(64);
        }

        public void setPageBit(CodePageRange range, boolean enable) {
            binary[range.bit] = enable;
        }

        public void setPageBit(CodePageRange range) {
            binary[range.bit] = true;
        }
    }
}
