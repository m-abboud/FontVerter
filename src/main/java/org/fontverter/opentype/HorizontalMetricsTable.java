package org.fontverter.opentype;

import org.fontverter.FontWriter;

import java.io.IOException;

public class HorizontalMetricsTable extends OpenTypeTable
{
    private int[] advanceWidth;
    private short[] leftSideBearing;
    private short[] nonHorizontalLeftSideBearing;
    private int numHMetrics;


    @Override
    public byte[] getData() throws IOException
    {
        FontWriter writer = FontWriter.createWriter();

        int bytesRead = 0;
        for (int i = 0; i < numHMetrics; i++)
        {
            writer.writeUnsignedShort(advanceWidth[i]);
            writer.writeShort(leftSideBearing[i]);
            bytesRead += 4;
        }

        return writer.toByteArray();
    }

    @Override
    public String getName()
    {
        return "hmtx";
    }

    public static HorizontalMetricsTable createEmptyTable()
    {
        HorizontalMetricsTable table = new HorizontalMetricsTable();
        table.numHMetrics = 1;
        table.advanceWidth = new int[]{1};
        table.leftSideBearing = new short[]{1};
        table.nonHorizontalLeftSideBearing = new short[]{1};
        return table;
    }
}
