package org.fontverter.cff;


import org.apache.fontbox.EncodedFont;
import org.apache.fontbox.cff.*;
import org.apache.fontbox.encoding.Encoding;
import org.fontverter.*;
import org.fontverter.opentype.OtfToWoffConverter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CffFontAdapter implements FontAdapter {
    private byte[] data = new byte[]{};
    private CFFFont font;

    public static CffFontAdapter parse(byte[] cffData) throws IOException {
        CFFFont cfffont = fontboxParse(cffData);
        CffFontAdapter font = new CffFontAdapter(cfffont);
        font.setData(cffData);
        return font;
    }

    private static CFFFont fontboxParse(byte[] cffData) throws IOException {
        CFFParser parser = new CFFParser();
        List<CFFFont> fonts = parser.parse(cffData);
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
        //byte[] start = Arrays.copyOfRange(fontFile, 0, 10);
//        return FontVerterUtils.bytesStartsWith(start, new byte[]{1, 0, 4, 4});
        return false;
    }

    public void read(byte[] fontFile) throws IOException {
        font = fontboxParse(fontFile);
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        if (fontFormat == FontVerter.FontFormat.OTF)
            return new CFFToOpenTypeConverter(this);
        if (fontFormat == FontVerter.FontFormat.WOFF)
            return new CombinedFontConverter(new CFFToOpenTypeConverter(this), new OtfToWoffConverter());

        throw new FontNotSupportedException("CFF to " + fontFormat + " conversion is not supported");
    }

    public CFFFont getFont() {
        return font;
    }

    public String getFullName() {
        String name = font.getName();
        if (name.isEmpty())
            name = nonNullDictEntry("FullName", String.class);

        return name;
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

    public Map<Integer, String> getGlyphIdsToNames() throws IOException {
        try {
            // reflection to get private map field for lazyness, !fragile!, obviously
            Field mapField = FontVerterUtils.findPrivateField("gidToName", CFFCharset.class);

            return (Map<Integer, String>) mapField.get(font.getCharset());
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public Encoding getEncoding() {
        if (font instanceof EncodedFont) {
            try {
                return ((EncodedFont) font).getEncoding();
            } catch (IOException e) {
                return CFFStandardEncoding.getInstance();
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
