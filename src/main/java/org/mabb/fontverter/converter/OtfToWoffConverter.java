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

package org.mabb.fontverter.converter;

import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.opentype.*;
import org.mabb.fontverter.woff.WoffFont;

import java.io.IOException;

public class OtfToWoffConverter implements FontConverter {
    OpenTypeFont otfFont;
    private WoffFont woffFont;
    protected int woffVersion = 1;

    public OtfToWoffConverter() {
    }

    public FVFont convertFont(FVFont font) throws IOException {
        otfFont = (OpenTypeFont) font;
        woffFont = WoffFont.createBlankFont(woffVersion);
        woffFont.addFont(font);
        addFontTables();

        return woffFont;
    }

    private void addFontTables() throws IOException {
        for (OpenTypeTable tableOn : otfFont.getTables())
            woffFont.addFontTable(tableOn.getUnpaddedData(), tableOn.getTableType(), tableOn.getChecksum());
    }

    public static class OtfToWoff2Converter extends OtfToWoffConverter {
        public OtfToWoff2Converter() {
            woffVersion = 2;
        }
    }
}
