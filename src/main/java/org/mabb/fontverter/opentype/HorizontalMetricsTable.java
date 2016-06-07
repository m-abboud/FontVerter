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
import org.mabb.fontverter.cff.CffFontAdapter.Glyph;
import org.mabb.fontverter.io.FontDataOutputStream;

import java.io.IOException;
import java.util.List;

public class HorizontalMetricsTable extends OpenTypeTable {
    private int[] advanceWidths;

    private short[] leftSideBearings;
    private short[] nonHorizontalLeftSideBearing;
    private int numHMetrics;
    public String getTableType() {
        return "hmtx";
    }

    /* big old kludge to handle conversion of tables types that arn't deserializable/parsable yet remove asap*/
    protected boolean isParsingImplemented() {
        return false;
    }

    protected byte[] generateUnpaddedData() throws IOException {
        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);

        for (int i = 0; i < numHMetrics; i++) {
            writer.writeUnsignedShort(advanceWidths[i]);
            writer.writeShort(leftSideBearings[i]);
        }
        for (int i = 0; i < nonHorizontalLeftSideBearing.length; i++)
            writer.writeUnsignedShort(nonHorizontalLeftSideBearing[i]);

        return writer.toByteArray();
    }

    public static HorizontalMetricsTable createDefaultTable(OpenTypeFont font) {
        HorizontalMetricsTable table = new HorizontalMetricsTable();
        table.font = font;

        table.nonHorizontalLeftSideBearing = new short[]{};
        table.leftSideBearings = new short[]{};
        table.advanceWidths = new int[]{};

        return table;
    }


    void normalize() throws IOException {
        advanceWidths = new int[]{};
        leftSideBearings = new short[]{};

        // ttf type doesn't appear to need these set for being valid at least so leaving alone atm
        // cff will throw validation errors if you don't copy from the cff table
        if (font.isCffType()) {
            CffFontAdapter cff = font.getCffTable().getCffFont();
            List<Glyph> glyphs = cff.getGlyphs();

            // must start with the .notdef entry otherwise removed
            glyphs.add(0, cff.createGlyph());

            advanceWidths = new int[glyphs.size()];
            leftSideBearings = new short[glyphs.size()];

            for (int i = 0; i < glyphs.size(); i++) {
                Glyph glyphOn = glyphs.get(i);
                advanceWidths[i] = glyphOn.getAdvanceWidth();
                leftSideBearings[i] = (short) glyphOn.getLeftSideBearing();
            }
        }

        numHMetrics = advanceWidths.length;

        if (font.getCmap() != null)
            loadMetrics();
    }

    private void loadMetrics() {
        int lsbArrCount = font.getCmap().getGlyphCount() - numHMetrics;
        if (lsbArrCount > 0) {
            nonHorizontalLeftSideBearing = new short[lsbArrCount];
            for (int i = 0; i < lsbArrCount; i++)
                nonHorizontalLeftSideBearing[i] = 1;
        } else
            nonHorizontalLeftSideBearing = new short[]{};
    }

    public int[] getAdvanceWidths() {
        return advanceWidths;
    }

    public short[] getLeftSideBearings() {
        return leftSideBearings;
    }
}
