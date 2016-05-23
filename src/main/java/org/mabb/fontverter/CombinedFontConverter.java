package org.mabb.fontverter;

import java.io.IOException;

public class CombinedFontConverter implements FontConverter {
    private final FontConverter[] converters;

    public CombinedFontConverter(FontConverter... converters) {
        this.converters = converters;
    }

    public FVFont convertFont(FVFont font) throws IOException {
        FVFont convertedFont = font;
        for (FontConverter converterOn : converters)
            convertedFont = converterOn.convertFont(convertedFont);

        return convertedFont;
    }
}
