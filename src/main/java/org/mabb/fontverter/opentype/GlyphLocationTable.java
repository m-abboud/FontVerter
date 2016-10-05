/*
 * Copyright (C) Maddie Abboud 2016
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

import org.mabb.fontverter.GlyphMapReader;
import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.DataTypeProperty;
import org.mabb.fontverter.io.DataTypeProperty.DataType;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class GlyphLocationTable extends OpenTypeTable {
    private static final Logger log = getLogger(GlyphLocationTable.class);

    @DataTypeProperty(dataType = DataType.USHORT, isArray = true, ignoreIf = "isLongOffsets", arrayLength = "getNumGlyphs")
    Integer[] shortOffsets;

    @DataTypeProperty(dataType = DataType.ULONG, isArray = true, includeIf = "isLongOffsets", arrayLength = "getNumGlyphs")
    Long[] longOffsets;

    public String getTableType() {
        return "loca";
    }

    public int getNumGlyphs() {
        return font.getMxap().getNumGlyphs() + 1;
    }

    public boolean isLongOffsets() {
        return font.getHead().isLongIndexToLocFormat();
    }

    void normalize() throws IOException {
        GlyphTable glyf = font.getGlyfTable();
        if (glyf == null)
            return;

        List<Long> offsets = new LinkedList<Long>();
        offsets.add(0L);

        int posOn = 0;
        for (TtfGlyph glyphOn : glyf.getGlyphs()) {
            posOn += (long) glyphOn.generateData().length;
            offsets.add((long) posOn);
        }

        if (isLongOffsets()) {
            longOffsets = offsets.toArray(new Long[offsets.size()]);
        } else {
            shortOffsets = new Integer[offsets.size()];
            for (int i = 0; i < offsets.size(); i++) {
                shortOffsets[i] = (int) (offsets.get(i) / 2);
            }
        }
    }

    public Long[] getOffsets() {
        if (isLongOffsets())
            return longOffsets;

        Long[] calcedShortOffsets = new Long[shortOffsets.length];
        for (int i = 0; i < shortOffsets.length; i++)
            calcedShortOffsets[i] = (long) (shortOffsets[i] * 2);

        return calcedShortOffsets;
    }
}
