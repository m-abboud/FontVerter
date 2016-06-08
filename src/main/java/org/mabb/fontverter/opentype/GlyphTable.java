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

import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.DataTypeProperty;
import org.mabb.fontverter.io.FontDataInputStream;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.mabb.fontverter.io.DataTypeProperty.DataType.SHORT;
import static org.mabb.fontverter.io.DataTypeProperty.DataType.USHORT;

public class GlyphTable extends OpenTypeTable {
    List<TtfGlyph> glyphs = new LinkedList<TtfGlyph>();

    public String getTableType() {
        return "glyf";
    }

    /* kludge to avoid incomplete writing/parsing from a parsed font remove soon*/
    protected boolean isParsingImplemented() {
        return false;
    }

    public void readData(byte[] data) throws IOException {
        super.readData(data);
        FontDataInputStream reader = new FontDataInputStream(data);
        Long[] offsets = font.getLocaTable().getOffsets();

        for (int i = 0; i < offsets.length - 1; i++) {
            Long offset = offsets[i];
            if (offset >= data.length)
                continue;

            reader.seek(offset.intValue());

            DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
            TtfGlyph glyph = (TtfGlyph) deserializer.deserialize(reader, TtfGlyph.class);

            glyphs.add(glyph);
        }
    }

    public static class TtfGlyph {
        @DataTypeProperty(dataType = SHORT)
        short numberOfContours;

        @DataTypeProperty(dataType = SHORT)
        short xMin;

        @DataTypeProperty(dataType = SHORT)
        short yMin;

        @DataTypeProperty(dataType = SHORT)
        short xMax;

        @DataTypeProperty(dataType = SHORT)
        short yMax;

        @DataTypeProperty(dataType = USHORT, isArray = true, arrayLength = "getNumberOfContours", ignoreIf = "isComposite")
        Integer[] endPtsOfContours;

        public short getNumberOfContours() {
            return numberOfContours;
        }

        public boolean isComposite() {
            return numberOfContours < 0;
        }
    }
}
