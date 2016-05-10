package org.fontverter;

import org.fontverter.io.ByteSerializerException;

import java.io.IOException;
import java.util.Map;

public interface FontAdapter {
    byte[] getData() throws IOException;

    boolean detectFormat(byte[] fontFile);

    void read(byte[] fontFile) throws IOException;

    FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException;
}
