package org.fontverter.cff;


import org.apache.fontbox.cff.CFFCharset;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.fontverter.FontNotSupportedException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CffFontContainer {
    private byte[] data = new byte[]{};

    public static CffFontContainer parse(byte[] cffData) throws IOException {
        CFFParser parser = new CFFParser();
        List<CFFFont> fonts = parser.parse(cffData);
        if (fonts.size() > 1)
            throw new FontNotSupportedException("Multiple CFF fonts in one file are not supported.");

        CffFontContainer font = new CffFontContainer(fonts.get(0));
        font.setData(cffData);
        return font;
    }

    public CFFFont getFont() {
        return font;
    }

    private final CFFFont font;

    public CffFontContainer(CFFFont font) {
        this.font = font;
    }

    public String getFullName() {
        String name = font.getName();
        if(name.isEmpty())
            name = nonNullDictEntry("FullName", String.class);

        return name;
    }

    public String getFamilyName() {
        String name = nonNullDictEntry("FamilyName", String.class);
        if(name.isEmpty())
            name = nonNullDictEntry("FullName", String.class);

        return name;
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

        if(obj != null && obj instanceof ArrayList)
            boundingBox = (ArrayList<Integer>) obj;

        if(boundingBox == null || boundingBox.size() < 4)
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

    public Map<Integer, String> getGlyphIdsToNames() throws IOException {
        try {
            // reflection to get private map field for lazyness, !fragile!, obviously
            Class type = CFFCharset.class;
            Field[] fields = type.getDeclaredFields();

            Field mapField = null;
            for (Field fieldOn : fields) {
                if (fieldOn.getName().contains("gidToName")) {
                    mapField = fieldOn;
                    mapField.setAccessible(true);
                }
            }

            return (Map<Integer, String>) mapField.get(font.getCharset());
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private <X> X nonNullDictEntry(String key, Class<X> type) {
        Object value = font.getTopDict().get(key);
        if (value != null)
            return (X) value;

        if (type == String.class)
            return (X) "";

        if (type == Integer.class)
            return (X) new Integer(1);

        return (X) "";
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
