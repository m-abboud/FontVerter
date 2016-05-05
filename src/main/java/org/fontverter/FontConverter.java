package org.fontverter;

import java.io.IOException;

public interface FontConverter {
    FontAdapter convertFont(FontAdapter font) throws IOException;
}
