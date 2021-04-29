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

import org.mabb.fontverter.FontProperties;
import org.mabb.fontverter.cff.CffFontAdapter;

class OpenTypeFontProperties extends FontProperties {
    static OpenTypeFontProperties createFrom(OpenTypeFont font) {
        OpenTypeFontProperties properties = new OpenTypeFontProperties();
        if (font.isCffType()) {
            properties.setMimeType("application/x-font-opentype");
            properties.setFileEnding("otf");
            properties.setCssFontFaceFormat("opentype");

            // not sure if cff names should read from name table or cff table
            // cff table appears to produce better results
            readCffTableNames(font, properties);
        } else {
            properties.setMimeType("application/x-font-truetype");
            properties.setFileEnding("ttf");
            properties.setCssFontFaceFormat("truetype");
            readNameTableNames(font, properties);
        }

        return properties;
    }

    private static void readNameTableNames(OpenTypeFont font, FontProperties properties) {
        // some ugly PDF ttf fonts have no name table.
        // possibley need to create ourselves from the composite font data
        if (font.getNameTable() == null)
            return;

        properties.setFullName(font.getNameTable().getName(OtfNameConstants.RecordType.FULL_FONT_NAME));
        properties.setName(font.getNameTable().getName(OtfNameConstants.RecordType.FULL_FONT_NAME));
        properties.setTrademarkNotice(font.getNameTable().getName(OtfNameConstants.RecordType.COPYRIGHT));
        properties.setVersion(font.getNameTable().getName(OtfNameConstants.RecordType.VERSION_STRING));
        properties.setSubFamilyName(font.getNameTable().getName(OtfNameConstants.RecordType.FONT_SUB_FAMILY));
        properties.setFamily(font.getNameTable().getName(OtfNameConstants.RecordType.FONT_FAMILY));
    }

    private static void readCffTableNames(OpenTypeFont font, FontProperties properties) {
        CffFontAdapter cff = font.getCffTable().getCffFont();

        properties.setFullName(cff.getFullName());
        properties.setName(cff.getName());
        properties.setVersion(cff.getVersion());
        properties.setSubFamilyName(cff.getSubFamilyName());
        properties.setTrademarkNotice(cff.getTrademarkNotice());
    }
}
