package org.mabb.fontverter.cff;

import org.mabb.fontverter.CharsetConverter;
import org.mabb.fontverter.CharsetConverter.GlyphMapping;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontConverter;
import org.mabb.fontverter.opentype.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        return new OtfFontAdapter(generateFont());
    }

    public OpenTypeFont generateFont() throws IOException {
        otfFont = OpenTypeFont.createBlankFont();
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
        Map<Integer, String> glyphIdsToNames = cffFont.getGlyphIdsToNames();
        if (glyphIdsToNames.containsKey(0))
            glyphIdsToNames.remove(0);

        List<GlyphMapping> glyphMappings =
                CharsetConverter.glyphMappingToEncoding(glyphIdsToNames, cffFont.getEncoding());

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
        otfFont.head.setMinX((short) cffFont.getMinX());
        otfFont.head.setMaxX((short) cffFont.getMaxX());

        otfFont.head.setMinY((short) cffFont.getMinY());
        otfFont.head.setMaxY((short) cffFont.getMaxY());
    }

}