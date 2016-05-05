package org.fontverter.woff;

import org.fontverter.io.ByteDataOutputStream;
import org.fontverter.io.ByteDataProperty;
import org.fontverter.io.ByteSerializerException;
import org.fontverter.opentype.OpenTypeTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WoffFont {
    private List<WoffTable> tables = new ArrayList<WoffTable>();

    public static WoffFont createBlankFont() {
        WoffFont font = new WoffFont();
        font.tables.add(new WoffTable.WoffHeader());
        return font;

    }

    public byte[] getFontData() throws IOException {
        ByteDataOutputStream out = new ByteDataOutputStream(ByteDataOutputStream.openTypeCharset);
        for (WoffTable tableOn : tables)
            out.write(tableOn.getData());

        return out.toByteArray();
    }

}
