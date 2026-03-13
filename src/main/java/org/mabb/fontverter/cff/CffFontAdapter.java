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

package org.mabb.fontverter.cff;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.EncodedFont;
import org.apache.fontbox.cff.*;
import org.apache.fontbox.encoding.Encoding;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontNotSupportedException;
import org.mabb.fontverter.FontProperties;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.converter.CFFToOpenTypeConverter;
import org.mabb.fontverter.converter.CombinedFontConverter;
import org.mabb.fontverter.converter.FontConverter;
import org.mabb.fontverter.converter.OtfToWoffConverter;
import org.mabb.fontverter.opentype.GlyphMapReader;
import org.mabb.fontverter.validator.RuleValidator;

import java.io.IOException;
import java.util.*;

public class CffFontAdapter implements FVFont {
    private byte[] data = new byte[]{};
    private CFFFont font;
    private static final Log LOG = LogFactory.getLog(CffFontAdapter.class);

    public static CffFontAdapter parse(byte[] cffData) throws IOException {
        CFFFont cfffont = fontboxParse(cffData);
        CffFontAdapter font = new CffFontAdapter(cfffont);
        font.setData(cffData);
        return font;
    }

    private static CFFFont fontboxParse(byte[] cffData) throws IOException {
        CFFParser parser = new CFFParser();
        List<CFFFont> fonts = parser.parse(cffData, new CFFParser.ByteSource() {
            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }
        });
        if (fonts.size() > 1)
            throw new FontNotSupportedException("Multiple CFF fonts in one file are not supported.");
        return fonts.get(0);
    }

    public CffFontAdapter(CFFFont font) {
        this.font = font;
    }

    public CffFontAdapter() {
    }

    public boolean detectFormat(byte[] fontFile) {
        try {
            // cff has no magic header so check if parseable to detect if cff
            fontboxParse(fontFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void read(byte[] fontFile) throws IOException {
        font = fontboxParse(fontFile);
        data = fontFile;
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        if (fontFormat == FontVerter.FontFormat.OTF)
            return new CFFToOpenTypeConverter(this);
        if (fontFormat == FontVerter.FontFormat.WOFF1)
            return new CombinedFontConverter(new CFFToOpenTypeConverter(this), new OtfToWoffConverter());
        if (fontFormat == FontVerter.FontFormat.WOFF2)
            return new CombinedFontConverter(new CFFToOpenTypeConverter(this), new OtfToWoffConverter.OtfToWoff2Converter());

        throw new FontNotSupportedException("Font conversion not supported");
    }

    public String getName() {
        String name = font.getName();
        if (name.isEmpty())
            name = nonNullDictEntry("FullName", String.class);

        return name;
    }

    public CFFFont getFont() {
        return font;
    }

    public String getFullName() {
        return nonNullDictEntry("FullName", String.class);
    }

    public String getFamilyName() {
        String name = nonNullDictEntry("FamilyName", String.class);
        if (name.isEmpty())
            name = nonNullDictEntry("FullName", String.class);

        return name;
    }

    public String getSubFamilyName() {
        return nonNullDictEntry("Weight", String.class);
    }

    public String getVersion() {
        return nonNullDictEntry("version", String.class);
    }

    public String getTrademarkNotice() {
        return nonNullDictEntry("Notice", String.class);
    }

    public Integer getUnderLinePosition() {
        return nonNullDictEntry("UnderlinePosition", Integer.class);
    }

    public int getMinX() {
        return getBoundingBox().get(0);
    }

    public int getMinY() {
        return getBoundingBox().get(1);
    }

    public int getMaxX() {
        return getBoundingBox().get(2);
    }

    public int getMaxY() {
        return getBoundingBox().get(3);
    }

    private ArrayList<Integer> getBoundingBox() {
        Object obj = font.getTopDict().get("FontBBox");
        ArrayList<Integer> boundingBox = null;

        if (obj != null && obj instanceof ArrayList)
            boundingBox = (ArrayList<Integer>) obj;

        if (boundingBox == null || boundingBox.size() < 4)
            boundingBox = createDefaultBoundingBox();

        return boundingBox;
    }

    private ArrayList<Integer> createDefaultBoundingBox() {
        // default is actually 0 0 0 0, but using reasonable filler vals here if we don't have a bbox
        // for maybe a better result
        ArrayList<Integer> boundingBox;
        boundingBox = new ArrayList<Integer>();
        boundingBox.add(30);
        boundingBox.add(-2);
        boundingBox.add(1300);
        boundingBox.add(800);
        return boundingBox;
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, String> getGlyphIdsToNames() throws IOException {

        Map<Integer, String> codeToNameMap = getEncoding().getCodeToNameMap();

        Map<Integer, String> gidToName = new HashMap();

        // key: sidorcid, value: name
        for (Map.Entry<Integer, String> entry : codeToNameMap.entrySet()) {
            if (!font.getCharset().isCIDFont()) {
                int sid = font.getCharset().getSID(entry.getValue());
                int gid = font.getCharset().getGIDForSID(sid);
                gidToName.put(gid, entry.getValue());
            } else {

                int gid = font.getCharset().getGIDForCID(entry.getKey());
                gidToName.put(gid, entry.getValue());
            }
        }
        return gidToName;


    }

    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> getCharCodeToGlyphIds() throws IOException {


        Map<Integer, String> codeToNames = getEncoding().getCodeToNameMap();

        Map<Integer, Integer> glyphIds = new HashMap();

        // key = sidorcid, value = name
        for (Map.Entry<Integer, String> entry : codeToNames.entrySet()) {
            if (!font.getCharset().isCIDFont()) {
                int gid = font.getCharset().getGIDForSID(entry.getKey());
                glyphIds.put(entry.getKey(), gid);
            } else {
                int gid = font.getCharset().getGIDForCID(entry.getKey());
                glyphIds.put(entry.getKey(), gid);
            }
        }


        return glyphIds;


    }

    public List<GlyphMapReader.GlyphMapping> getGlyphMaps() throws IOException {
        Map<Integer, String> glyphIdsToNames = getGlyphIdsToNames();
        if (glyphIdsToNames.size() != 0)
            return GlyphMapReader.readGlyphsToNames(glyphIdsToNames, getEncoding());

        return GlyphMapReader.readCharCodesToGlyphs(getCharCodeToGlyphIds(), getEncoding());
    }


    public Encoding getEncoding() {
        if (font instanceof EncodedFont) {
            Encoding encoding = ((EncodedFont) font).getEncoding();
            if (encoding.getCodeToNameMap().values().size() > 1) {
                return encoding;
            }
        }

        return CFFStandardEncoding.getInstance();
    }


    private <X> X nonNullDictEntry(String key, Class<X> type) {
        Object value = font.getTopDict().get(key);
        if (value != null)
            return (X) value;

        if (type == String.class)
            return (X) "";

        if (type == Integer.class)
            return (X) Integer.valueOf(1);

        return (X) "";
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void normalize() {
    }

    public boolean isValid() {
        return true;
    }

    public List<RuleValidator.FontValidatorError> getValidationErrors() {
        return new ArrayList<RuleValidator.FontValidatorError>();
    }

    public FontProperties getProperties() {
        FontProperties properties = new FontProperties();
        properties.setMimeType("");
        properties.setFileEnding("cff");
        properties.setCssFontFaceFormat("");
        return properties;
    }

    public List<CffGlyph> getGlyphs() throws IOException {
        List<CffGlyph> glyphs = new ArrayList<CffGlyph>();

        for (GlyphMapReader.GlyphMapping mapOn : getGlyphMaps()) {
            CffGlyph glyph = createGlyph();

            String charString = font.toString().substring(font.toString().indexOf("charStrings="));
            String[] charStrings = charString.replace("charStrings=", "").replace("[[", "").replace("]", "").split("\\[");

            Type2CharStringParser parser = new Type2CharStringParser(font.getName());

            Type2CharString charStr = font.getType2CharString(mapOn.glyphId);
            byte[] bytes = null;

            if (charStr.getGID() < charStrings.length) {
                bytes = font.getCharStringBytes().get(charStr.getGID());
            }
            if (bytes == null) {
                bytes = font.getCharStringBytes().get(0);
            }

            glyph.charStr = charStr;


            byte[][] globalSubrIndex = new byte[getFont().getGlobalSubrIndex().size()][];

            for (int i = 0; i < getFont().getGlobalSubrIndex().size(); i++) {
                globalSubrIndex[i] = getFont().getGlobalSubrIndex().get(i);
            }


            List<Object> result = parser.parse(bytes, globalSubrIndex,
                    null,
                    mapOn.name);
            glyph.readType2Sequence(result);

            glyph.map = mapOn;
            glyphs.add(glyph);

        }

        return glyphs;
    }


    public Integer getDefaultWidth() {
        String key = "defaultWidthX";
        if (!getPrivateDict().containsKey(key))
            return 1000;

        return (Integer) getPrivateDict().get(key);
    }

    public Integer getNominalWidth() {
        String key = "nominalWidthX";

        if (!getPrivateDict().containsKey(key))
            return 1000;

        return (Integer) getPrivateDict().get(key);
    }

    Map<String, Object> getPrivateDict() {
        if (font instanceof CFFType1Font)
            return ((CFFType1Font) font).getPrivateDict();
        else {
            Map<String, Object> dict = new HashMap<String, Object>();
            for (Map<String, Object> dictOn : ((CFFCIDFont) font).getPrivDicts())
                dict.putAll(dictOn);

            return dict;
        }
    }

    public CffGlyph createGlyph() {
        CffGlyph glyph = new CffGlyph();
        glyph.nominalWidth = getNominalWidth();
        glyph.defaultWidth = getDefaultWidth();
        glyph.advancedWidth = getDefaultWidth();

        return glyph;
    }

    public static class CffGlyph {
        private int leftSideBearing = 0;
        private Integer advancedWidth;
        Integer nominalWidth;
        Integer defaultWidth;
        public GlyphMapReader.GlyphMapping map;
        public Type2CharString charStr;

        public int getLeftSideBearing() {
            return leftSideBearing;
        }

        public void setLeftSideBearing(int leftSideBearing) {
            this.leftSideBearing = leftSideBearing;
        }

        public int getAdvanceWidth() {
            return advancedWidth;
        }

        public void setAdvancedWidth(int advancedWidth) {
            this.advancedWidth = advancedWidth;
        }

        public void readType2Sequence(List<Object> type2Sequence) {
            Object firstObj = type2Sequence.get(0);

            if (firstObj instanceof Integer)
                advancedWidth = nominalWidth + (Integer) firstObj;
            else
                advancedWidth = defaultWidth;

            parseHints(type2Sequence);
        }

        private void parseHints(List<Object> type2Sequence) {
            List<Command> commands = new LinkedList<Command>();

            Command commandOn = null;
            for (Object objOn : type2Sequence) {

                if (objOn instanceof CharStringCommand) {
                    CharStringCommand command = (CharStringCommand) objOn;
                    commandOn = new Command();
                    commandOn.name = command.toString();
                    commands.add(commandOn);

                } else if (objOn instanceof Integer && commandOn != null)
                    commandOn.values.add((Integer) objOn);
            }

            for (Command command : commands) {
                parseHint(command);
            }
        }

        private void parseHint(Command command) {
            if (command.name.toLowerCase().startsWith("hstem")) {
                leftSideBearing = command.values.get(0);
            }
        }

        static class Command {
            String name;
            List<Integer> values = new LinkedList<Integer>();
        }


    }
}
