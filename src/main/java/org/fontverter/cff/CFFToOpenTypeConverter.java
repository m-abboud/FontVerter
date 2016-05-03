package org.fontverter.cff;

import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.CFFStandardEncoding;
import org.fontverter.CharsetConverter;
import org.fontverter.FontNotSupportedException;
import org.fontverter.opentype.CffTable;
import org.fontverter.opentype.FontSerializerException;
import org.fontverter.opentype.NameTable;
import org.fontverter.opentype.OpenTypeFont;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CffToOpenTypeConverter {

    private final byte[] cffData;
    private CffFontContainer cffFont;
    private OpenTypeFont otfFont;

    public CffToOpenTypeConverter(byte[] cffData) {
        this.cffData = cffData;
    }

    public OpenTypeFont generateFont() throws IOException, FontSerializerException {
        CFFParser parser = new CFFParser();
        List<CFFFont> fonts = parser.parse(cffData);
        if (fonts.size() > 1)
            throw new FontNotSupportedException("Multiple CFF fonts in one file are not supported.");

        cffFont = new CffFontContainer(fonts.get(0));
        otfFont = OpenTypeFont.createBlankFont();

        otfFont.addTable(new CffTable(this.cffData));

        convertGlyphIdToCodeMap();
        convertNameRecords();

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

    private void convertNameRecords() throws IOException {
        NameTable name = otfFont.name;
        name.setFontFamily(cffFont.getFamilyName());
        name.setVersion(cffFont.getVersion());
        name.setFontSubFamily(cffFont.getSubFamilyName());
        name.setPostScriptName(cffFont.getFamilyName());
        name.setCopyright(cffFont.getTrademarkNotice());
        name.setFontFullName(cffFont.getFullName());
    }
}