package org.fontverter.woff;

import org.fontverter.*;
import org.fontverter.io.ByteDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class WoffFont implements FontAdapter {
    protected WoffHeader header;
    protected List<WoffTable> tables = new ArrayList<WoffTable>();
    protected List<FontAdapter> fonts = new ArrayList<FontAdapter>();

    public static WoffFont createBlankFont(int version) {
        WoffFont font;

        if (version == 1) {
            font = new Woff1Font();
            font.header = WoffHeader.createWoff1Header();
        } else {
            font = new Woff2Font();
            font.header = WoffHeader.createWoff2Header();
        }

        return font;
    }

    WoffFont() {
    }

    public abstract WoffTable createTable();

    public abstract void addFontTable(byte[] unpaddedData, WoffConstants.TableFlagType tableFlag, long checksum);

    public byte[] getData() throws IOException {
        // have to write out data twice for header calcs
        header.calculateValues(this);
        return getRawData();
    }

    byte[] getRawData() throws IOException {
        ByteDataOutputStream out = new ByteDataOutputStream(ByteDataOutputStream.OPEN_TYPE_CHARSET);

        Collections.sort(tables, new Comparator<WoffTable>() {
            public int compare(WoffTable o1, WoffTable o2) {
                String c1 = o1.flag.toString();
                String c2 = o2.flag.toString();
                return c1.compareTo(c2);
            }
        });
        out.write(header.getData());
        out.write(getTableDirectoryData());
        out.write(getCompressedDataBlock());

        return out.toByteArray();
    }

    byte[] getTableDirectoryData() throws IOException {
        ByteDataOutputStream writer = new ByteDataOutputStream(ByteDataOutputStream.OPEN_TYPE_CHARSET);
        for (WoffTable tableOn : tables)
            writer.write(tableOn.getDirectoryData());

        return writer.toByteArray();
    }

    byte[] getCompressedDataBlock() throws IOException {
        ByteDataOutputStream writer = new ByteDataOutputStream(ByteDataOutputStream.OPEN_TYPE_CHARSET);
        for (WoffTable tableOn : tables)
            writer.write(tableOn.getCompressedData());

        return writer.toByteArray();
    }

    public void read(byte[] fontFile) throws IOException {
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        throw new FontNotSupportedException("Font conversion not supported");
    }

    public void addFont(FontAdapter adapter) {
        fonts.add(adapter);
    }

    public List<FontAdapter> getFonts() {
        return fonts;
    }

    public List<WoffTable> getTables() {
        return tables;
    }

    public int getCompressedSize() throws IOException {
        return getCompressedDataBlock().length;
    }
}
