package org.mabb.fontverter.opentype;

import org.mabb.fontverter.CharsetConverter;
import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.DataTypeBindingSerializer;
import org.mabb.fontverter.io.DataTypeProperty;
import org.mabb.fontverter.io.DataTypeProperty.DataType;

import java.io.IOException;
import java.util.List;

import static org.mabb.fontverter.CharsetConverter.*;

public class PostScriptTable extends OpenTypeTable {
    @DataTypeProperty(dataType = DataType.FIXED32)
    private float version;

    @DataTypeProperty(dataType = DataType.FIXED32)
    private float italicAngle;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short underlinePosition;

    @DataTypeProperty(dataType = DataType.SHORT)
    private short underlineThickness;

    @DataTypeProperty(dataType = DataType.ULONG)
    private long isFixedPitch;

    @DataTypeProperty(dataType = DataType.ULONG)
    private long minMemType42;

    @DataTypeProperty(dataType = DataType.ULONG)
    private long maxMemType42;

    @DataTypeProperty(dataType = DataType.ULONG)
    private long mimMemType1;

    @DataTypeProperty(dataType = DataType.ULONG)
    private long maxMemType1;

    @DataTypeProperty(dataType = DataType.USHORT, includeIf = "isVersion2")
    private int numGlyphs = 0;

    @DataTypeProperty(dataType = DataType.USHORT, isArray = true, includeIf = "isVersion2", arrayLength = "getNumGlyphs")
    private Integer[] glyphNameIndex = new Integer[0];

    @DataTypeProperty(dataType = DataType.PASCAL_STRING, isArray = true, includeIf = "isVersion2", arrayLength = "getNumGlyphs")
    private String[] glyphNames = new String[0];

    public String getTableType() {
        return "post";
    }

    public static PostScriptTable createDefaultTable(float version) {
        PostScriptTable table = new PostScriptTable();
        table.version = version;
        table.italicAngle = 0.0f;
        table.underlinePosition = -143;
        table.underlineThickness = 20;
        table.isFixedPitch = 0;
        table.minMemType42 = 0;
        table.maxMemType42 = 0;
        table.mimMemType1 = 0;
        table.maxMemType1 = 0;

        return table;
    }

    public void readData(byte[] data) throws IOException {
        // sometimes read ttf's glypname array goes past the table's data length, see ttfs.pdf/volvo owners manual
        // unsure of why they do atm, possibly just a corrupt postscript table.
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        deserializer.setRecoverFromEOF(true);
        deserializer.deserialize(data, this);
    }

    public float getVersion() {
        return version;
    }

    public float getItalicAngle() {
        return italicAngle;
    }

    public short getUnderlinePosition() {
        return underlinePosition;
    }

    public short getUnderlineThickness() {
        return underlineThickness;
    }

    public long getIsFixedPitch() {
        return isFixedPitch;
    }

    public long getMinMemType42() {
        return minMemType42;
    }

    public long getMaxMemType42() {
        return maxMemType42;
    }

    public long getMimMemType1() {
        return mimMemType1;
    }

    public long getMaxMemType1() {
        return maxMemType1;
    }

    public boolean isVersion2() {
        return getVersion() == 2;
    }

    public int getNumGlyphs() {
        if (numGlyphs > 257)
            return numGlyphs - 258;

        return numGlyphs;
    }

    void normalize() {
        if (font.getCmap() != null)
            loadGlyphsFromCmap();

        super.normalize();
    }

    private void loadGlyphsFromCmap() {
        numGlyphs = font.getCmap().getGlyphCount();
        if (numGlyphs < 1)
            return;

        font.getCmap().getGlyphMappings();

        glyphNameIndex = new Integer[numGlyphs];
        glyphNames = new String[numGlyphs];

        List<GlyphMapping> mappings = font.getCmap().getGlyphMappings();
        for (int i = 0; i < numGlyphs - 1; i++) {
            GlyphMapping entryOn = mappings.get(0);

            glyphNameIndex[i] = entryOn.glyphId;
            glyphNames[i] = entryOn.name;
        }

        glyphNameIndex[numGlyphs - 1] = 0;
        glyphNames[numGlyphs - 1] = ".notdef";
    }
}
