package org.mabb.fontverter;

import java.io.IOException;

public interface FontConverter {
    FVFont convertFont(FVFont font) throws IOException;
}
