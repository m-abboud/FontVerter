package org.fontverter.woff;

import com.google.common.collect.Table;
import org.fontverter.io.ByteDataOutputStream;
import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OpenTypeTable;

import java.io.IOException;
import java.util.List;

public class Woff1Font extends WoffFont {
    public static WoffFont createBlankFont() {
        Woff1Font font = new Woff1Font();
        font.header = WoffHeader.createWoff1Header();
        return font;
    }


    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag) {
        FontTable table = new FontTable.WoffV1FontTable(data, flag);
        tables.add(table);
    }

    protected Ta
    public byte[] getData() {

    }

    private final int WOFF1_TABLE_DIRECTORY_ENTRY_SIZE = 4 * 5;

    private void calculateOffsets() throws IOException {
        // must calculate table record offsets before we write any table data
        // start data offsets after sfnt header and table records
        int offset = tables.size() * WOFF1_TABLE_DIRECTORY_ENTRY_SIZE + OpenTypeFont.SFNT_HEADER_SIZE;
        for (FontTable.WoffV1FontTable tableOn : tables) {
            tableOn.setOffset(offset);
            offset += tableOn.getData().length;
        }
    }

}
