package org.fontverter.woff;

import org.fontverter.*;
import org.fontverter.io.ByteDataOutputStream;
import org.fontverter.io.ByteDataProperty;
import org.fontverter.io.ByteSerializerException;
import org.fontverter.opentype.OpenTypeTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WoffFont implements FontAdapter {
    private List<WoffTable> tables = new ArrayList<WoffTable>();

    public static WoffFont createBlankFont() {
        WoffFont font = new WoffFont();
        font.tables.add(new WoffTable.WoffHeader());
        return font;
    }

    public byte[] getData() throws IOException {
        ByteDataOutputStream out = new ByteDataOutputStream(ByteDataOutputStream.openTypeCharset);
        for (WoffTable tableOn : tables)
            out.write(tableOn.getData());

        return out.toByteArray();
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "wOF2", "wOFF");
    }

    public void read(byte[] fontFile) throws IOException {

    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        return null;
    }

    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag) {
        FontTable table = new FontTable(data, flag);
        tables.add(table);
    }
}
