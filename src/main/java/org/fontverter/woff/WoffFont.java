package org.fontverter.woff;

import org.fontverter.*;
import org.fontverter.io.ByteDataOutputStream;
import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OtfFontAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WoffFont implements FontAdapter {
    protected WoffHeader header;
    protected List<FontTable> tables = new ArrayList<FontTable>();
    protected List<FontAdapter> fonts = new ArrayList<FontAdapter>();

    public static WoffFont createBlankFont() {
        WoffFont font = new WoffFont();
        font.header = WoffHeader.createWoff2Header();
        return font;
    }

    public List<FontTable> getTables() {
        return tables;
    }

    public byte[] getData() throws IOException {
        // have to write out data twice for header calcs
        header.calculateValues(this);
        return getRawData();
    }

    byte[] getRawData() throws IOException {
        ByteDataOutputStream out = new ByteDataOutputStream(ByteDataOutputStream.openTypeCharset);

        out.write(header.getData());
        out.write(getTableDirectoryData());
        out.write(getCompressedDataBlock());

        return out.toByteArray();
    }

    byte[] getTableDirectoryData() throws IOException {
        ByteDataOutputStream writer = new ByteDataOutputStream(ByteDataOutputStream.openTypeCharset);
        for (FontTable tableOn : tables)
            writer.write(tableOn.getDirectoryData());

        return writer.toByteArray();
    }

    byte[] getCompressedDataBlock() throws IOException {
        ByteDataOutputStream writer = new ByteDataOutputStream(ByteDataOutputStream.openTypeCharset);
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

    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag) {
        FontTable table = new FontTable(data, flag);
        tables.add(table);
    }

    public List<FontAdapter> getFonts() {
        return fonts;
    }
}
