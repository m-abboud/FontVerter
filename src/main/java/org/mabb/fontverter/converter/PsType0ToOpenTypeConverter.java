package org.mabb.fontverter.converter;

import org.apache.fontbox.cff.CFFStandardEncoding;
import org.apache.fontbox.cmap.CMap;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.mabb.fontverter.CharsetConverter;
import org.mabb.fontverter.CharsetConverter.GlyphMapping;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerterUtils;
import org.mabb.fontverter.opentype.CmapTable;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OpenTypeParser;
import org.mabb.fontverter.opentype.OtfFontAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PsType0ToOpenTypeConverter {
    private OpenTypeFont otfFont;
    private PDType0Font type0Font;
    private int originalNumGlyphs;

    public FVFont convert(PDType0Font type0Font) throws IOException, IllegalAccessException, InstantiationException {
        this.type0Font = type0Font;
        PDCIDFont descendantFont = type0Font.getDescendantFont();

        if (!(descendantFont instanceof PDCIDFontType2))
            throw new IOException("Can only convert type 0 fonts with ttf type descendant.");

        byte[] ttfData = type0Font.getFontDescriptor().getFontFile2().toByteArray();
        OpenTypeParser otfParser = new OpenTypeParser();
        otfFont = otfParser.parse(ttfData);
        originalNumGlyphs = otfFont.getMxap().getNumGlyphs();

        // so the descendant ttf font will usually have some important tables missing from it
        // that we need to create ourselves from data in the parent type 0 font.
        if (otfFont.getCmap() == null)
            convertCmap();

        otfFont.finalizeFont();

        return new OtfFontAdapter(otfFont);
    }

    private void convertCmap() throws IllegalAccessException {
        Map<Integer, String> charToUnicode = getType0CharToUnicode();
        List<GlyphMapping> glyphMappings =
                CharsetConverter.glyphIdsToNameToEncoding(charToUnicode, CFFStandardEncoding.getInstance());

        // not sure where mssing glyph cmap records coming from? maybe pdfbox not reading correctly.
        // to fix anyway fudge it by adding missing cmap entries
        // wait todo see if can just not add missing ones?
        if (glyphMappings.size() < originalNumGlyphs - 1) {
            int glyphsNeeded = originalNumGlyphs - glyphMappings.size() - 1;
            for (int i = 0; i < glyphsNeeded; i++) {
                int code = CharsetConverter.findNextAvailableCharCode(glyphMappings, CFFStandardEncoding.getInstance());
                String name = CFFStandardEncoding.getInstance().getName(code);
                int glyphId = findNextMissingGlyphId(glyphMappings);

                glyphMappings.add(new GlyphMapping(glyphId, code, name));
            }
        }
        // todo needa refactor bunch of stuff for different plat/encode/lang table char glyph map reads
        CmapTable cmapTable = CmapTable.createDefaultTable();
        cmapTable.addGlyphMapping(glyphMappings);

        otfFont.setCmap(cmapTable);
    }

    private int findNextMissingGlyphId(List<GlyphMapping> glyphMappings) {
        for (int i = 1; i < glyphMappings.size(); i++) {
            boolean found = false;
            for (GlyphMapping entryOn : glyphMappings)
                if (entryOn.glyphId == i)
                    found = true;

            if(!found)
                return i;
        }

        return glyphMappings.size() + 1;
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
        Object mappings = FontVerterUtils.findPrivateField("charToUnicode", cmap.getClass()).get(cmap);
        if (mappings == null)
            return new HashMap<Integer, String>();

        return (Map<Integer, String>) mappings;
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Integer> getType0CodeToCid(CMap cmap) throws IllegalAccessException {
        Object mappings = (Map<Integer, Integer>) FontVerterUtils.findPrivateField("codeToCid", cmap.getClass()).get(cmap);
        if (mappings == null)
            return new HashMap<Integer, Integer>();

        return (Map<Integer, Integer>) mappings;
    }

}
