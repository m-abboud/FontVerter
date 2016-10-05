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

package org.mabb.fontverter;

import java.io.IOException;

public class CombinedFontConverter implements FontConverter {
    private final FontConverter[] converters;

    public CombinedFontConverter(FontConverter... converters) {
        this.converters = converters;
    }

    public FVFont convertFont(FVFont font) throws IOException {
        FVFont convertedFont = font;
        for (FontConverter converterOn : converters)
            convertedFont = converterOn.convertFont(convertedFont);

        return convertedFont;
    }
}
