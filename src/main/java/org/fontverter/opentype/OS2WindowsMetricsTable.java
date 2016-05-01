package org.fontverter.opentype;

import org.fontverter.FontWriter;

import java.io.IOException;

class OS2WindowsMetricsTable extends OpenTypeTable
{
    private int version;
    private short averageCharWidth;
    private int weightClass;
    private int widthClass;
    private short fsType;
    private short subscriptXSize;
    private short subscriptYSize;
    private short subscriptXOffset;
    private short subscriptYOffset;
    private short superscriptXSize;
    private short superscriptYSize;
    private short superscriptXOffset;
    private short superscriptYOffset;
    private short strikeoutSize;
    private short strikeoutPosition;
    private int familyClass;
    private byte[] panose = new byte[10];
    private long unicodeRange1;
    private long unicodeRange2;
    private long unicodeRange3;
    private long unicodeRange4;
    private String achVendId;
    private int fsSelection;
    private int firstCharIndex;
    private int lastCharIndex;
    private int typoAscender;
    private int typoDescender;
    private int typoLineGap;
    private int winAscent;
    private int winDescent;
    private long codePageRange1;
    private long codePageRange2;
    private int sxHeight;
    private int sCapHeight;
    private int usDefaultChar;
    private int usBreakChar;
    private int usMaxContext;

    @Override
    public String getName()
    {
        return "OS/2";
    }

    public byte[] getData() throws IOException
    {
        FontWriter out = FontWriter.createWriter();
        out.writeUnsignedShort(version);
        out.writeShort(averageCharWidth);
        out.writeUnsignedShort(weightClass);
        out.writeUnsignedShort(widthClass);
        out.writeShort(fsType);
        out.writeShort(subscriptXSize);
        out.writeShort(subscriptYSize);
        out.writeShort(subscriptXOffset);
        out.writeShort(subscriptYOffset);
        out.writeShort(superscriptXSize);
        out.writeShort(superscriptYSize);
        out.writeShort(superscriptXOffset);
        out.writeShort(superscriptYOffset);
        out.writeShort(strikeoutSize);
        out.writeShort(strikeoutPosition);
        out.writeUnsignedShort(familyClass);
        out.write(panose);
        out.writeLong(unicodeRange1);
        out.writeLong(unicodeRange2);
        out.writeLong(unicodeRange3);
        out.writeLong(unicodeRange4);
        out.writeString(achVendId);
        out.writeUnsignedShort(fsSelection);
        out.writeUnsignedShort(firstCharIndex);
        out.writeUnsignedShort(lastCharIndex);
        out.writeShort(typoAscender);
        out.writeShort(typoDescender);
        out.writeShort(typoLineGap);
        out.writeUnsignedShort(winAscent);
        out.writeUnsignedShort(winDescent);
        if (version >= 1)
        {
            out.writeLong(codePageRange1);
            out.writeLong(codePageRange2);
        }
        if (version >= 2)
        {
            out.writeShort(sxHeight);
            out.writeUnsignedShort(sCapHeight);
            out.writeUnsignedShort(usDefaultChar);
            out.writeUnsignedShort(usBreakChar);
            out.writeUnsignedShort(usMaxContext);
        }
        return out.toByteArray();
    }

    public static OS2WindowsMetricsTable createEmptyTable()
    {
        OS2WindowsMetricsTable table = new OS2WindowsMetricsTable();
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
}
