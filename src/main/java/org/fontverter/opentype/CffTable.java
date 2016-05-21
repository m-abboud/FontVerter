package org.fontverter.opentype;

import org.fontverter.io.DataTypeSerializerException;
import org.fontverter.opentype.OpenTypeTable;

public class CffTable extends OpenTypeTable {
    private byte[] data;

    public CffTable(byte[] data) {
        this.data = data;
    }

    public CffTable() {
    }

    protected byte[] generateUnpaddedData() {
        return data;
    }

    public String getName() {
        return "CFF ";
    }

    public void readData(byte[] data) throws DataTypeSerializerException {
        this.data = data;
    }
}
