package org.fontverter.woff;

import org.fontverter.io.ByteDataOutputStream;
import org.fontverter.woff.WoffConstants.TableFlagType;

import java.io.IOException;

public class FontTable extends WoffTable {
    private final byte[] tableData;
    private final TableFlagType flag;

    public FontTable(byte[] table, TableFlagType flag) {
        this.tableData = table;
        this.flag = flag;
    }

    public byte[] getData() throws IOException {
        WoffOutputStream writer = new WoffOutputStream();
        writer.writeUnsignedInt8(flag.getValue());
        // tag would be here but spec says optional and it's same as flag
        writer.writeUIntBase128(tableData.length);
        // transformedlength here

        return writer.toByteArray();
    }
}
