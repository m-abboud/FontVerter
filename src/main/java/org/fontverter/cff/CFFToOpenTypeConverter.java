package org.fontverter.cff;

import org.apache.fontbox.cff.CFFStandardEncoding;
import org.fontverter.CharsetConverter;
import org.fontverter.opentype.*;

import java.io.IOException;
import java.util.Map;

public class CffToOpenTypeConverter {
    private CffFontContainer cffFont;
    private OpenTypeFont otfFont;

    public CffToOpenTypeConverter(CffFontContainer cffFont) {
        this.cffFont = cffFont;
    }

    public CffToOpenTypeConverter(byte[] cffdata) throws IOException {
        this.cffFont = CffFontContainer.parse(cffdata);
    }

    public OpenTypeFont generateFont() throws IOException, FontSerializerException {
        otfFont = OpenTypeFont.createBlankFont();
        byte[] cffData = cffFont.getData();
        otfFont.addTable(new CffTable(cffData));

        convertGlyphIdToCodeMap();
        convertNameRecords(otfFont.name);
        convertHorizontalLayoutSettings();

        // kinda kludgy having to call normalize after font is edited
        otfFont.normalizeTables();
        return otfFont;
    }

    private void convertGlyphIdToCodeMap() throws IOException {
        Map<Integer, String> glyphIdsToNames = cffFont.getGlyphIdsToNames();
        if(glyphIdsToNames.containsKey(0))
            glyphIdsToNames.remove(0);

        Map<Integer, Integer> otfIdToCharCodes =
                CharsetConverter.nameMapToEncoding(glyphIdsToNames, CFFStandardEncoding.getInstance());

        otfFont.cmap.addGlyphMapping(otfIdToCharCodes);
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
        otfFont.hhea.descender = cffFont.getUnderLinePosition().shortValue();
        otfFont.head.setMinX((short) cffFont.getMinX());
        otfFont.head.setMaxX((short) cffFont.getMaxX());
        
        otfFont.head.setMinY((short) cffFont.getMinY());
        otfFont.head.setMaxY((short) cffFont.getMaxY());
    }
}