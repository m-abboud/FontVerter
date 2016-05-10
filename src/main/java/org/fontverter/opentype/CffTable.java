package org.fontverter.opentype;

import org.fontverter.opentype.OpenTypeTable;

public class CffTable extends OpenTypeTable
{
    private byte[] data;

    public CffTable(byte[] data)
    {
        this.data = data;
    }

    public byte[] getUnpaddedData()
    {
        return data;
    }

    public String getName()
    {
        return "CFF ";
    }
}
