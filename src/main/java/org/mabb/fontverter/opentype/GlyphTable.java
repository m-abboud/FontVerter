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

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.FontDataOutputStream;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class GlyphTable extends OpenTypeTable {
    private static final Logger log = getLogger(GlyphTable.class);

    List<TtfGlyph> glyphs = new LinkedList<TtfGlyph>();

    public String getTableType() {
        return "glyf";
    }

    protected byte[] generateUnpaddedData() throws IOException {
        FontDataOutputStream out = new FontDataOutputStream();
        for (TtfGlyph glyphOn : glyphs) {
            if (!glyphOn.isEmpty())
                out.write(glyphOn.generateData());
        }

        return out.toByteArray();
    }

    public void readData(byte[] data) throws IOException {
        super.readData(data);
        FontDataInputStream reader = new FontDataInputStream(data);
        Long[] offsets = font.getLocaTable().getOffsets();

        for (int i = 0; i < offsets.length - 1; i++) {
            Long offset = offsets[i];
            long length = offsets[i + 1] - offset;

            // 0 length is valid and means an empty outline for glyph
            if (length == 0) {
                glyphs.add(new TtfGlyph());
                continue;
            }

            if (offset >= data.length) {
                log.error("Invalid loca table offset, offset greater than glyf table length");
                continue;
            }

            try {
                reader.seek(offset.intValue());
                byte[] glyphData = reader.readBytes((int) length);

                TtfGlyph glyph = TtfGlyph.parse(glyphData, font);
                glyphs.add(glyph);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<TtfGlyph> getGlyphs() {
        return glyphs;
    }

    public List<TtfGlyph> getNonEmptyGlyphs() {
        List<TtfGlyph> nonEmpty = new LinkedList<TtfGlyph>();

        for (TtfGlyph glyphOn : glyphs)
            if (!glyphOn.isEmpty())
                nonEmpty.add(glyphOn);

        return nonEmpty;
    }

}
