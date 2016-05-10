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
    protected List<FontTable> tables = new ArrayList<FontTable>();
    protected List<FontAdapter> fonts = new ArrayList<FontAdapter>();

//    public static WoffFont createBlankFont() {
//        WoffFont font = new WoffFont();
//        font.header = WoffHeader.createWoff2Header();
//        return font;
//    }

    public List<FontTable> getTables() {
        return tables;
    }

    public byte[] getData() throws IOException {
        // have to write out data twice for header calcs
        header.calculateValues(this);
        return getRawData();
    }

    byte[] getRawData() throws IOException {
        ByteDataOutputStream out = new ByteDataOutputStream(ByteDataOutputStream.OPEN_TYPE_CHARSET);

        Collections.sort(tables, new Comparator<FontTable>() {
            public int compare(FontTable o1, FontTable o2) {
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
        for (FontTable tableOn : tables)
            writer.write(tableOn.getDirectoryData());

        return writer.toByteArray();
    }

    byte[] getCompressedDataBlock() throws IOException {
        ByteDataOutputStream writer = new ByteDataOutputStream(ByteDataOutputStream.OPEN_TYPE_CHARSET);
        for (FontTable tableOn : tables)
            writer.write(tableOn.getCompressedTableData());

        return writer.toByteArray();
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "wOF2", "wOFF");
    }

    public void read(byte[] fontFile) throws IOException {
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        throw new FontNotSupportedException("Font conversion not supported");
    }

    public void addFont(FontAdapter adapter) {
        fonts.add(adapter);
    }

    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag, long checksum) {
        FontTable table = new FontTable(data, flag);
        tables.add(table);
    }

    public List<FontAdapter> getFonts() {
        return fonts;
    }
}
