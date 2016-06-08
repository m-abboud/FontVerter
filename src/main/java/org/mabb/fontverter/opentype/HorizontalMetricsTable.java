/*
 * Copyright (C) Matthew Abboud 2016
 *
 * FontVerter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FontVerter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FontVerter. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mabb.fontverter.opentype;

import org.mabb.fontverter.cff.CffFontAdapter;
import org.mabb.fontverter.cff.CffFontAdapter.CffGlyph;
import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.FontDataOutputStream;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class HorizontalMetricsTable extends OpenTypeTable {
    private static final Logger log = getLogger(HorizontalMetricsTable.class);
    private int[] advanceWidths;

    private short[] leftSideBearings;
    private Short[] nonHorizontalLeftSideBearing;

    public String getTableType() {
        return "hmtx";
    }

    public void readData(byte[] data) throws IOException {
        FontDataInputStream reader = new FontDataInputStream(data);

        int numHMetrics = font.getHhea().numberOfHMetrics;
        advanceWidths = new int[numHMetrics];
        leftSideBearings = new short[numHMetrics];

        for (int i = 0; i < numHMetrics; i++) {
            advanceWidths[i] = reader.readUnsignedShort();
            leftSideBearings[i] = reader.readShort();
        }

        LinkedList<Short> nonHorzBearings = new LinkedList<Short>();
        while (reader.available() >= 2)
            nonHorzBearings.add(reader.readShort());

        nonHorizontalLeftSideBearing = nonHorzBearings.toArray(new Short[nonHorzBearings.size()]);
    }

    protected byte[] generateUnpaddedData() throws IOException {
        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);

        for (int i = 0; i < advanceWidths.length; i++) {
            writer.writeUnsignedShort(advanceWidths[i]);
            writer.writeShort(leftSideBearings[i]);
        }
        for (Short bearingOn : nonHorizontalLeftSideBearing)
            writer.writeUnsignedShort(bearingOn);

        return writer.toByteArray();
    }

    public static HorizontalMetricsTable createDefaultTable(OpenTypeFont font) {
        HorizontalMetricsTable table = new HorizontalMetricsTable();
        table.font = font;

        table.nonHorizontalLeftSideBearing = new Short[]{};
        table.leftSideBearings = new short[]{};
        table.advanceWidths = new int[]{};

        return table;
    }


    void normalize() throws IOException {
        if (advanceWidths == null) {
            leftSideBearings = new short[]{0};
            advanceWidths = new int[]{1000};
        }

        // todo for ttf type
        if (font.isCffType()) {
            CffFontAdapter cff = font.getCffTable().getCffFont();
            List<CffGlyph> glyphs = cff.getGlyphs();

            // must start with the .notdef entry otherwise removed
            if (glyphs.get(0).getLeftSideBearing() != 0)
                glyphs.add(0, cff.createGlyph());

            advanceWidths = new int[glyphs.size()];
            leftSideBearings = new short[glyphs.size()];

            for (int i = 0; i < glyphs.size(); i++) {
                CffGlyph glyphOn = glyphs.get(i);
                advanceWidths[i] = glyphOn.getAdvanceWidth();
                leftSideBearings[i] = (short) glyphOn.getLeftSideBearing();
            }
        }

        if (font.getCmap() != null)
            loadMetrics();
    }

    private void loadMetrics() {
        if (isFromParsedFont)
            return;

        int lsbArrCount = font.getCmap().getGlyphCount() - advanceWidths.length;
        if (lsbArrCount > 0) {
            nonHorizontalLeftSideBearing = new Short[lsbArrCount];
            for (int i = 0; i < lsbArrCount; i++)
                nonHorizontalLeftSideBearing[i] = 1;
        } else
            nonHorizontalLeftSideBearing = new Short[]{};
    }

    public int[] getAdvanceWidths() {
        return advanceWidths;
    }

    public short[] getLeftSideBearings() {
        return leftSideBearings;
    }
}
