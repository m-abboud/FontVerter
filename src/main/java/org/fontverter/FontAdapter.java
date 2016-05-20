package org.fontverter;

import java.io.IOException;

public interface FontAdapter {
    byte[] getData() throws IOException;

    boolean detectFormat(byte[] fontFile);

    void read(byte[] fontFile) throws IOException;

    FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException;
}
