package org.fontverter;

import java.io.IOException;

/**
 * Less of an adapter and more of a base font class. Silly prefixed name to avoid confusion with jdk Font
 */
public interface FVFont {
    byte[] getData() throws IOException;

    boolean detectFormat(byte[] fontFile);

    void read(byte[] fontFile) throws IOException;

    // todo: tear this method out and move converter stuff to seperate converter package so dependencies
    // between font types not all messy like?
    FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException;

    String getFontName();
}
