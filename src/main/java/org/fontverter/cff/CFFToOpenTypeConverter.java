package org.fontverter.cff;

import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.fontverter.FontNotSupportedException;
import org.fontverter.FontWriter;
import org.fontverter.opentype.CffTable;
import org.fontverter.opentype.FontSerializerException;
import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OpenTypeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class CffToOpenTypeConverter {

    private final byte[] cffData;

    public CffToOpenTypeConverter(byte[] cffData) {
        this.cffData = cffData;
    }

    public OpenTypeFont generateFont() throws IOException, FontSerializerException {
        CFFParser parser = new CFFParser();
        List<CFFFont> fonts = parser.parse(cffData);
        if(fonts.size() > 1)
            throw new FontNotSupportedException("Multiple CFF fonts in one file are not supported.");

        CFFFont inputFont = fonts.get(0);
        OpenTypeFont font = OpenTypeFont.createBlankFont();
        font.addTable(new CffTable(this.cffData));

        return font;
    }

}