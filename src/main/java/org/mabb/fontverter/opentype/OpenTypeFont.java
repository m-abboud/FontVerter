package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.FontDataOutputStream;
import org.mabb.fontverter.io.DataTypeBindingSerializer;
import org.mabb.fontverter.io.DataTypeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.mabb.fontverter.opentype.OpenTypeFont.SfntHeader.CFF_FLAVOR;

/**
 * OpenType covers both .otf and .ttfs , .otf is for CFF type fonts and .ttf is used for TrueType outline fonts.
 * Microsft's OpenType is built on type of the original apple TrueType spec
 * OpenType spec can be found here: https://www.microsoft.com/typography/otspec/otff.htm
 * Apple TrueType spec can be found here: https://developer.apple.com/fonts/TrueType-Reference-Manual
 */
public class OpenTypeFont {
    public static final int SFNT_HEADER_SIZE = 12;

    public SfntHeader sfntHeader;
    public List<OpenTypeTable> tables;
    public HeadTable head;

    private static Logger log = LoggerFactory.getLogger(OpenTypeFont.class);
    private File sourceFile;

    public static OpenTypeFont createBlankFont() {
        OpenTypeFont font = new OpenTypeFont();
        font.head = font.initTable(HeadTable.createDefaultTable());

        font.initTable(OS2WinMetricsTable.createDefaultTable());
        font.initTable(HorizontalHeadTable.createDefaultTable());
        font.initTable(MaximumProfileTable.createDefaultTable());

        font.initTable(PostScriptTable.createDefaultTable(3));
        font.initTable(CmapTable.createDefaultTable());
        font.initTable(HorizontalMetricsTable.createDefaultTable(font));

        font.initTable(NameTable.createDefaultTable());
        font.normalizeTables();
        return font;
    }

    public OpenTypeFont() {
        tables = new ArrayList<OpenTypeTable>();
        sfntHeader = new SfntHeader();
    }

    private <T extends OpenTypeTable> T initTable(T table) {
        table.font = this;
        tables.add(table);
        return table;
    }

    public void addTable(OpenTypeTable table) {
        tables.add(table);
    }

    private List<OpenTypeTable> descendingSortedTables() {
        // OpenType spec says tables must be sorted alphabetically, but then later states some very specific order
        // then still needs to be added here for seperate data entries
        Collections.sort(tables, new Comparator<OpenTypeTable>() {
            public int compare(OpenTypeTable left, OpenTypeTable right) {
                return left.getTableTypeName().compareTo(right.getTableTypeName());
            }
        });

        return tables;
    }

    public byte[] getFontData() throws IOException {
        // offsets and gotta calc checksums before doing final full font checksum so calling the data write out
        // twice to be lazy
        finalizeFont();

        // now we for realsies write out the font bytes
        return getRawData();
    }

    public void finalizeFont() throws IOException {
        // gott make sure checksums = 0 before doing calc
        for (OpenTypeTable tableOn : tables)
            tableOn.resetCalculations();

        descendingSortedTables();
        normalizeTables();
        calculateOffsets(tables);

        for (OpenTypeTable tableOn : tables)
            tableOn.finalizeRecord();

        // head checksum has to be very last after other checksums + offsets calculated so just grab full byte
        // output to calc instead of trying to re-edit the byte array at the right place
        getHead().checksumAdjustment(getRawData());
    }

    private void normalizeTables() {
        if (getCmap() != null && getMxap() != null)
            getMxap().setNumGlyphs(getCmap().getGlyphCount());

        for (OpenTypeTable tableOn : tables) {
            tableOn.font = this;
            tableOn.normalize();
        }
    }

    private byte[] getRawData() throws IOException {
        FontDataOutputStream out = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        sfntHeader.setNumTables(tables.size());

        out.write(sfntHeader.getData());

        for (OpenTypeTable tableOn : tables)
            out.write(tableOn.getRecordData());

        for (OpenTypeTable tableOn : tables)
            out.write(tableOn.getData());

        return out.toByteArray();
    }

    private void calculateOffsets(List<OpenTypeTable> tables) throws IOException {
        // must calculate table record offsets before we write any table data
        // start data offsets after sfnt header and table records
        int offset = tables.size() * OpenTypeTable.TABLE_RECORD_SIZE + OpenTypeFont.SFNT_HEADER_SIZE;
        for (OpenTypeTable tableOn : tables) {
            tableOn.setOffset(offset);
            offset += tableOn.getData().length;
        }
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void removeTable(Class toRemoveType) {
        OpenTypeTable toRemoveTable = null;
        for (OpenTypeTable tableOn : tables) {
            if (tableOn.getClass() == toRemoveType)
                toRemoveTable = tableOn;
        }

        if (toRemoveTable != null)
            tables.remove(toRemoveTable);
    }

    private <T extends OpenTypeTable> T findTableType(Class type) {
        for (OpenTypeTable tableOn : tables) {
            if (tableOn.getClass() == type)
                return (T) tableOn;
        }

        return null;
    }

    private void setTable(OpenTypeTable toAdd) {
        removeTable(toAdd.getClass());
        tables.add(toAdd);
    }

    public boolean isCffType() {
        return sfntHeader.sfntFlavor.equals(CFF_FLAVOR);
    }

    public boolean isTrueTypeOutlineType() {
        return !sfntHeader.sfntFlavor.equals(CFF_FLAVOR);
    }

    public float getOpenTypeVersion() {
        return sfntHeader.openTypeVersion();
    }

    public HeadTable getHead() {
        return findTableType(HeadTable.class);
    }

    public void setHead(HeadTable head) {
        setTable(head);
    }

    public HorizontalHeadTable getHhea() {
        return findTableType(HorizontalHeadTable.class);
    }

    public void setHhea(HorizontalHeadTable hhea) {
        setTable(hhea);
    }

    public HorizontalMetricsTable getHmtx() {
        return findTableType(HorizontalMetricsTable.class);
    }

    public void setHmtx(HorizontalMetricsTable hmtx) {
        setTable(hmtx);
    }

    public OS2WinMetricsTable getOs2() {
        return findTableType(OS2WinMetricsTable.class);
    }

    public void setOs2(OS2WinMetricsTable os2) {
        setTable(os2);
    }

    public PostScriptTable getPost() {
        return findTableType(PostScriptTable.class);
    }

    public void setPost(PostScriptTable post) {
        setTable(post);
    }

    public CmapTable getCmap() {
        return findTableType(CmapTable.class);
    }

    public void setCmap(CmapTable cmap) {
        setTable(cmap);
    }

    public MaximumProfileTable getMxap() {
        return findTableType(MaximumProfileTable.class);
    }

    public void setMxap(MaximumProfileTable mxap) {
        setTable(mxap);
    }

    public ControlValueTable getCvt() {
        return findTableType(ControlValueTable.class);
    }

    public void setCvt(ControlValueTable cvt) {
        setTable(cvt);
    }

    public NameTable getNameTable() {
        return findTableType(NameTable.class);
    }

    public void setName(NameTable name) {
        setTable(name);
    }

    static class SfntHeader {
        static String CFF_FLAVOR = "OTTO";
        static String VERSION_1 = "\u0000\u0001\u0000\u0000";
        static String VERSION_2 = "\u0000\u0001\u0000\u0000";
        static String VERSION_2_5 = "\u0000\u0001\u0005\u0000";

        @DataTypeProperty(dataType = DataTypeProperty.DataType.STRING, byteLength = 4)
        public String sfntFlavor = CFF_FLAVOR;

        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
        public int numTables;

        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
        public int searchRange;

        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
        public int entrySelector;

        @DataTypeProperty(dataType = DataTypeProperty.DataType.USHORT)
        public int rangeShift;

        public void setNumTables(int numTables) {
            this.numTables = numTables;
            searchRange = closestMaxPowerOfTwo(numTables) * 16;
            rangeShift = numTables * 16 - searchRange;
            entrySelector = (int) log2(closestMaxPowerOfTwo(numTables));
        }

        private int closestMaxPowerOfTwo(double number) {
            int powerOfTwo = 1;
            while (powerOfTwo * 2 < number)
                powerOfTwo = powerOfTwo * 2;

            return powerOfTwo;
        }

        private double log2(int number) {
            return Math.log(number) / Math.log(2);
        }

        byte[] getData() throws IOException {
            DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
            return serializer.serialize(this);
        }

        float openTypeVersion() {
            // string version consts are kludge for getting around data type version difference string vs fixed
            // so don't have to write extra data type annotation logic.
            if (sfntFlavor.equals(CFF_FLAVOR))
                return 3;
            if (sfntFlavor.equals(VERSION_2))
                return 2;
            if (sfntFlavor.equals(VERSION_2_5))
                return 2.5F;

            return 1;
        }
    }
}
