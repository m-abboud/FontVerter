package org.fontverter;

import java.io.IOException;

public class CombinedFontConverter implements FontConverter {
    private final FontConverter[] converters;

    public CombinedFontConverter(FontConverter... converters) {
        this.converters = converters;
    }

    public FontAdapter convertFont(FontAdapter font) throws IOException {
        FontAdapter convertedFont = font;
        for(FontConverter converterOn : converters)
            convertedFont = converterOn.convertFont(convertedFont);

        return convertedFont;
    }
}
