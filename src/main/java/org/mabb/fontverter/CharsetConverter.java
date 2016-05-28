package org.mabb.fontverter;

import org.apache.fontbox.encoding.Encoding;

import java.util.*;

public class CharsetConverter {
    public static List<GlyphMapping> glyphMappingToEncoding(Map<Integer, String> idToNames, Encoding encoding) {
        List<GlyphMapping> glyphMappings = new ArrayList<GlyphMapping>();
        Map<Integer, Integer> usedCodes = new HashMap<Integer, Integer>();

        for (Map.Entry<Integer, String> nameSetOn : idToNames.entrySet()) {
            String name = nameSetOn.getValue();
            int charCode = nameToCode(name, encoding, usedCodes);
            int glyphId = nameSetOn.getKey();

            glyphMappings.add(new GlyphMapping(glyphId, charCode, name));
        }

        return glyphMappings;
    }

    private static int nameToCode(String name, Encoding encoding, Map<Integer, Integer> usedCodes) {
        int code = 0;
        for (Map.Entry<Integer, String> entryOn : encoding.getCodeToNameMap().entrySet()) {
            int codeOn = entryOn.getKey();

            if (entryOn.getValue().equals(name) && !usedCodes.containsKey(codeOn))
                code = entryOn.getKey();
        }

        // glyph names can map to multiple id's so we have to remove the id from our search after mapping
        // a name to it
        usedCodes.put(code, 0);
        return code;
    }

    public static class GlyphMapping {
        public final Integer glyphId;
        public final Integer charCode;
        public final String name;

        public GlyphMapping(Integer glyphId, Integer charCode, String name) {
            this.glyphId = glyphId;
            this.charCode = charCode;
            this.name = name;
        }
    }
}
