package org.fontverter;

import org.fontverter.io.ByteSerializerException;

import java.io.IOException;
import java.util.Map;

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
    byte[] getData() throws IOException;

    boolean detectFormat(byte[] fontFile);

    void read(byte[] fontFile) throws IOException;

    FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException;
}
