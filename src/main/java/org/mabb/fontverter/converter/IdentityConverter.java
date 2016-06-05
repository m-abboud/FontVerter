package org.mabb.fontverter.converter;

import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontConverter;

import java.io.IOException;

public class IdentityConverter implements FontConverter {
    public FVFont convertFont(FVFont font) throws IOException {
        return font;
    }
}
