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
import org.mabb.fontverter.io.DataTypeSerializerException;

import java.io.IOException;
import java.util.List;

public class CffTable extends OpenTypeTable {
    private byte[] data;
    private CffFontAdapter cff;

    public CffTable(byte[] data) throws IOException {
        readData(data);
    }

    public CffTable() {
    }

    protected byte[] generateUnpaddedData() {
        return data;
    }

    public String getTableType() {
        return "CFF ";
    }

    public void readData(byte[] data) throws IOException {
        this.data = data;
        cff = new CffFontAdapter();
        cff.read(data);
    }

    public List<CffFontAdapter.Glyph> getGlyphs() throws IOException {
        return getCffFont().getGlyphs();
    }

    public CffFontAdapter getCffFont() throws IOException {
        return cff;
    }
}
