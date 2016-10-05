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

import org.mabb.fontverter.GlyphMapReader.GlyphMapping;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontConverter;
import org.mabb.fontverter.cff.CffFontAdapter;
import org.mabb.fontverter.opentype.*;

import java.io.IOException;
import java.util.List;

public class CFFToOpenTypeConverter implements FontConverter {
    private CffFontAdapter cffFont;
    private OpenTypeFont otfFont;

    public CFFToOpenTypeConverter(CffFontAdapter cffFont) {
        this.cffFont = cffFont;
    }

    public CFFToOpenTypeConverter(byte[] cffdata) throws IOException {
        this.cffFont = CffFontAdapter.parse(cffdata);
    }

    public FVFont convertFont(FVFont font) throws IOException {
        this.cffFont = (CffFontAdapter) font;
        return generateFont();
    }

    public OpenTypeFont generateFont() throws IOException {
        otfFont = OpenTypeFont.createBlankFont();
        otfFont.getSfntHeader().sfntFlavor = SfntHeader.CFF_FLAVOR;

        byte[] cffData = cffFont.getData();
        otfFont.addTable(new CffTable(cffData));

        convertGlyphIdToCodeMap();
        convertNameRecords(otfFont.getNameTable());
        convertHorizontalLayoutSettings();

        // kinda kludgy having to call normalize after font is edited
        otfFont.finalizeFont();

        return otfFont;
    }

    private void convertGlyphIdToCodeMap() throws IOException {
        List<GlyphMapping> glyphMappings = cffFont.getGlyphMaps();

        for (int i = 0; i < glyphMappings.size(); i++) {
            GlyphMapping mappingOn = glyphMappings.get(i);

            if (mappingOn.glyphId == 0) {
                glyphMappings.remove(mappingOn);
                break;
            }
        }

        otfFont.getCmap().addGlyphMapping(glyphMappings);
    }

    private void convertNameRecords(NameTable name) throws IOException {
        name.setFontFamily(cffFont.getFamilyName());
        name.setVersion(cffFont.getVersion());
        name.setFontSubFamily(cffFont.getSubFamilyName());
        name.setPostScriptName(cffFont.getFamilyName());
        name.setCopyright(cffFont.getTrademarkNotice());
        name.setFontFullName(cffFont.getFullName());
    }

    private void convertHorizontalLayoutSettings() throws IOException {
        otfFont.getHhea().descender = cffFont.getUnderLinePosition().shortValue();
        otfFont.getHead().setMinX((short) cffFont.getMinX());
        otfFont.getHead().setMaxX((short) cffFont.getMaxX());

        otfFont.getHead().setMinY((short) cffFont.getMinY());
        otfFont.getHead().setMaxY((short) cffFont.getMaxY());
    }

}