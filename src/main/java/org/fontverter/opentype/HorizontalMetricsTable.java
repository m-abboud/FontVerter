package org.fontverter.opentype;

import org.fontverter.io.FontDataOutputStream;

import java.io.IOException;

public class HorizontalMetricsTable extends OpenTypeTable {
    private int[] advanceWidth;
    private short[] leftSideBearing;
    private short[] nonHorizontalLeftSideBearing;
    private int numHMetrics;

    public String getTableTypeName() {
        return "hmtx";
    }

    /* big old kludge to handle conversion of tables types that arn't deserializable/parsable yet remove asap*/
    protected boolean isParsingImplemented() {
        return false;
    }

    protected byte[] generateUnpaddedData() throws IOException {
        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);

        for (int i = 0; i < numHMetrics; i++) {
            writer.writeUnsignedShort(advanceWidth[i]);
            writer.writeShort(leftSideBearing[i]);
        }
        for (int i = 0; i < nonHorizontalLeftSideBearing.length; i++)
            writer.writeUnsignedShort(nonHorizontalLeftSideBearing[i]);

        return writer.toByteArray();
    }


    public static HorizontalMetricsTable createDefaultTable(OpenTypeFont font) {
        HorizontalMetricsTable table = new HorizontalMetricsTable();
        table.font = font;

        table.nonHorizontalLeftSideBearing = new short[]{};
        table.leftSideBearing = new short[]{};
        table.advanceWidth = new int[]{};

        return table;
    }

    void normalize() {
        numHMetrics = 5;

        leftSideBearing = new short[]{0, 10, 29, 29, 55};
        advanceWidth = new int[]{1000, 1292, 1251, 1430, 1244};

        int lsbArrCount = font.getCmap().getGlyphCount() - numHMetrics;
        if (lsbArrCount > 0) {
            nonHorizontalLeftSideBearing = new short[lsbArrCount];
            for (int i = 0; i < lsbArrCount; i++)
                nonHorizontalLeftSideBearing[i] = 1;
        } else
            nonHorizontalLeftSideBearing = new short[]{};
    }
}
