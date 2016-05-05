package org.fontverter;

import java.io.IOException;
import java.util.Map;

// maybe interface should just be read write methods, different fonts kinda different and adapter doesnt fit too will
// but design patterns , because
public interface FontAdapter {
//    String getFullName();
//
//    String getFamilyName();
//
//    String getSubFamilyName();
//
//    String getVersion();
//
//    String getTrademarkNotice();
//
//    Integer getUnderLinePosition();
//
//    int getMinX();
//
//    int getMinY();
//
//    int getMaxX();
//
//    int getMaxY();
//
//    Map<Integer, String> getGlyphIdsToNames() throws IOException;
    byte[] getData();

    boolean detectFormat(byte[] fontFile);

    void read(byte[] fontFile) throws IOException;

    FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException;
}
