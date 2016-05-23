package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.DataTypeSerializerException;

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

    public String getTableTypeName() {
        return "CFF ";
    }

    public void readData(byte[] data) throws DataTypeSerializerException {
        this.data = data;
    }
}
