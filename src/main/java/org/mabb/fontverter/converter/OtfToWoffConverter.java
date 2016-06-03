package org.mabb.fontverter.converter;

import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontConverter;
import org.mabb.fontverter.opentype.*;
import org.mabb.fontverter.woff.WoffConstants.TableFlagType;
import org.mabb.fontverter.woff.WoffFont;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OtfToWoffConverter implements FontConverter {
    OpenTypeFont otfFont;
    private WoffFont woffFont;
    protected int woffVersion = 1;

    public OtfToWoffConverter() {
    }

    public FVFont convertFont(FVFont font) throws IOException {
        otfFont = (OpenTypeFont) font;
        woffFont = WoffFont.createBlankFont(woffVersion);
        woffFont.addFont(font);
        addFontTables();

        return woffFont;
    }

    private void addFontTables() throws IOException {
        for (OpenTypeTable tableOn : otfFont.getTables())
            woffFont.addFontTable(tableOn.getUnpaddedData(), tableOn.getTableType(), tableOn.getChecksum());
    }

    public static class OtfToWoff2Converter extends OtfToWoffConverter {
        public OtfToWoff2Converter() {
            woffVersion = 2;
        }
    }
}
