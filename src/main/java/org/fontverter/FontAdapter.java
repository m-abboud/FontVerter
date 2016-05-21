package org.fontverter;

import java.io.IOException;

public interface FontAdapter {
    byte[] getData() throws IOException;

    boolean detectFormat(byte[] fontFile);

    void read(byte[] fontFile) throws IOException;

    // todo: tear this method out and move converter stuff to seperate converter package so dependencies
    // between font types not all messy like?
    FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException;
}
