package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.DataTypeProperty;

import static org.mabb.fontverter.io.DataTypeProperty.*;

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

    public String getTableTypeName() {
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
        table.panose = new byte[]{2, 0, 6, 3, 0, 0, 0, 0, 0, 0};
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
        table.codePageRange1 = 1;
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
}
