package org.fontverter.cff;

import org.fontverter.opentype.OpenTypeTable;

class CFFTable extends OpenTypeTable
{
    private byte[] data;

    public CFFTable(byte[] data)
    {
        this.data = data;
    }

    public byte[] getData()
    {
        return data;
    }

    public String getName()
    {
        return "CFF ";
    }
}
