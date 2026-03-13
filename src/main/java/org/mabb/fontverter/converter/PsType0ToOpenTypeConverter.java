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

import org.apache.commons.lang3.StringUtils;
import org.apache.fontbox.cmap.CMap;
import org.apache.pdfbox.pdmodel.font.*;
import org.mabb.fontverter.opentype.GlyphMapReader.GlyphMapping;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.FontVerterUtils;
import org.mabb.fontverter.opentype.*;
import org.mabb.fontverter.opentype.OpenTypeFont;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PsType0ToOpenTypeConverter {
    private OpenTypeFont otfFont;
    private PDType0Font type0Font;

    public FVFont convert(PDType0Font type0Font) throws IOException, IllegalAccessException, InstantiationException {
        this.type0Font = type0Font;
        PDCIDFont descendantFont = type0Font.getDescendantFont();

        otfFont = getOtfFromDescendantFont(descendantFont);

        // so the descendant ttf font will usually have some important tables missing from it
        // that we need to create ourselves from data in the parent type 0 font.

        // always build cmap from type0 parent ourselves, cmaps existing in the ttf child tend to have some
        // issues in certain browsers and apps.
        convertCmap();
        if (otfFont.getNameTable() == null || isCffDescendant())
            convertNameRecords();

        otfFont.finalizeFont();
        otfFont.normalize();
        return otfFont;
    }

    private OpenTypeFont getOtfFromDescendantFont(PDCIDFont descendantFont) throws IOException, InstantiationException, IllegalAccessException {
        if (isTtfDescendant()) {
            byte[] ttfData = type0Font.getFontDescriptor().getFontFile2().toByteArray();
            OpenTypeParser otfParser = new OpenTypeParser();

            return otfParser.parse(ttfData);
        } else if (isCffDescendant())
            return buildFromCff();

        // don't think descendant can be anything but cff or ttf but just incase
        throw new IOException("Descendant font type not supported: " + descendantFont.getClass().getSimpleName());
    }

    private OpenTypeFont buildFromCff() throws IOException {
        byte[] cffData = type0Font.getFontDescriptor().getFontFile3().toByteArray();
        OpenTypeFont otfFont = (OpenTypeFont) FontVerter.convertFont(cffData, FontVerter.FontFormat.OTF);

        return otfFont;
    }

    private void convertCmap() throws IllegalAccessException, IOException {
        List<GlyphMapping> glyphMappings = new ArrayList<GlyphMapping>();

        Map<Integer, String> charToUnicode = getType0CharToUnicode();
        for (Map.Entry<Integer, String> nameSetOn : charToUnicode.entrySet()) {
            String name = nameSetOn.getValue();
            int charCode = name.charAt(0);
            int glyphId = nameSetOn.getKey();

            if (name.length() > 2 || charCode >= 0xFFFF)
                throw new IOException("Multi byte glyph name not supported.");

            if (charCode != 0)
                glyphMappings.add(new GlyphMapping(glyphId, charCode, name));
        }

        CmapTable cmapTable = CmapTable.createDefaultTable();
        cmapTable.addGlyphMapping(glyphMappings);

        otfFont.setCmap(cmapTable);
    }

    private void convertNameRecords() {
        NameTable names = NameTable.createDefaultTable();
        String fullName = type0Font.getName();
        String family = type0Font.getName();
        String subFamily = "Normal";

        Matcher matcher = Pattern.compile("([^-^+]*)(\\+|-)([^-]*)-?([^-]*)?").matcher(type0Font.getName());
        if (matcher.find()) {
            family = matcher.group(3);

            String subMatch = matcher.group(4);
            if (!StringUtils.isEmpty(subMatch) && !subMatch.equals("Identity"))
                subFamily = subMatch;
        }

        names.setFontFullName(fullName);
        names.setPostScriptName(family);
        names.setFontFamily(family);
        names.setFontSubFamily(subFamily);

        otfFont.setName(names);
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, String> getType0CharToUnicode() throws IllegalAccessException {
        CMap cmap = (CMap) FontVerterUtils.findPrivateField("toUnicodeCMap", PDFont.class).get(type0Font);
        if (cmap == null)
            return new HashMap<Integer, String>();

        return getCmapUnicodeMap(cmap);
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, String> getCmapUnicodeMap(CMap cmap) throws IllegalAccessException {
        Object mappings = FontVerterUtils.findPrivateField("charToUnicodeTwoBytes", cmap.getClass()).get(cmap);
        if (mappings == null)
            return new HashMap<Integer, String>();

        return (Map<Integer, String>) mappings;
    }

    private boolean isTtfDescendant() {
        return type0Font.getDescendantFont() instanceof PDCIDFontType2;
    }

    private boolean isCffDescendant() {
        return type0Font.getDescendantFont() instanceof PDCIDFontType0;
    }
}
