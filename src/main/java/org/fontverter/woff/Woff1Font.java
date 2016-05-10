package org.fontverter.woff;

import org.fontverter.io.ByteDataOutputStream;

import java.io.IOException;

public class Woff1Font extends WoffFont {
    private final int WOFF1_TABLE_DIRECTORY_ENTRY_SIZE = 4 * 5;
    private final int WOFF1_HEADER_SIZE = (9 * 4) + (4 * 2);

    public static WoffFont createBlankFont() {
        Woff1Font font = new Woff1Font();
        font.header = WoffHeader.createWoff1Header();
        return font;
    }

    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag, long checksum) {
        Woff1FontTable table = new Woff1FontTable(data, flag);
        table.checksum = checksum;
        tables.add(table);
    }

    byte[] getRawData() throws IOException {
        calculateOffsets();
        return super.getRawData();
    }
    
    byte[] getCompressedDataBlock() throws IOException {
        ByteDataOutputStream writer = new ByteDataOutputStream(ByteDataOutputStream.openTypeCharset);
        for (FontTable tableOn : tables)
            writer.write(tableOn.getCompressedTableData());

        return writer.toByteArray();
    }

    private void calculateOffsets() throws IOException {
        // must calculate table record offsets before we write any table data
        // start data offsets after sfnt header and table records
        int offset = tables.size() * WOFF1_TABLE_DIRECTORY_ENTRY_SIZE + WOFF1_HEADER_SIZE;
        for (FontTable table : tables) {
            Woff1FontTable tableOn = (Woff1FontTable) table;
            tableOn.setOffset(offset);
            offset += tableOn.getCompressedTableData().length;
        }
    }

}
