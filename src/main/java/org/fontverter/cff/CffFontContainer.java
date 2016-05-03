package org.fontverter.cff;


import org.apache.fontbox.cff.CFFCharset;
import org.apache.fontbox.cff.CFFFont;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CffFontContainer {
    private final CFFFont font;

    public CffFontContainer(CFFFont font) {
        this.font = font;
    }

    public String getFullName() {
        return nonNullDictEntry("FullName", String.class);
    }

    public String getFamilyName() {
        return nonNullDictEntry("FamilyName", String.class);
    }
    public String getSubFamilyName() {
        return nonNullDictEntry("Weight", String.class);
    }

    public String getVersion() {
        return nonNullDictEntry("Version", String.class);
    }

    public String getTrademarkNotice() {
        return nonNullDictEntry("Notice", String.class);
    }

    public Map<Integer, String> getGlyphIdsToNames() throws NoSuchFieldException, IllegalAccessException {
        // reflection to get private map field for lazyness, !fragile!, obviously
        Class type = font.getCharset().getClass();
        Field mapField = type.getField("gidToName");

        return (Map<Integer, String>) mapField.get(font.getCharset());
    }

    private <X> X nonNullDictEntry(String key, Class<X> type) {
        Object value = font.getTopDict().get(key);
        if(value != null)
            return (X) value;

        if(type == String.class)
            return (X) "";

        if(type == Integer.class)
            return (X) new Integer(1);

        return (X) "";
    }
}
