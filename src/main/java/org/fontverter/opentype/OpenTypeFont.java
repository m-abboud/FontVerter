package org.fontverter.opentype;

import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.fontverter.io.FontDataOutputStream;
import org.fontverter.io.DataTypeBindingSerializer;
import org.fontverter.io.DataTypeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        font.initTable(PostScriptTable.createDefaultTable());
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
                return left.getName().compareTo(right.getName());
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
        getMxap().setNumGlyphs(getCmap().getGlyphCount());

        for (OpenTypeTable tableOn : tables)
            tableOn.normalize();
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
        return findTableType(OS2WindowsMetricsTable.class);
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

    public NameTable getName() {
        return findTableType(NameTable.class);
    }

    public void setName(NameTable name) {
        setTable(name);
    }

    private <T extends OpenTypeTable> T findTableType(Class type) {
        for (OpenTypeTable tableOn : tables) {
            if (tableOn.getClass() == type)
                return (T) tableOn;
        }

        return null;
    }

    private void setTable(OpenTypeTable toAdd) {
        OpenTypeTable existingTable = null;
        for (OpenTypeTable tableOn : tables) {
            if (tableOn.getClass() == toAdd.getClass())
                existingTable = tableOn;
        }

        if (existingTable != null)
            tables.remove(existingTable);

        tables.add(toAdd);
    }

    public static class SfntHeader {
        @DataTypeProperty(dataType = DataTypeProperty.DataType.STRING, byteLength = 4)
        public String sfntFlavor = "OTTO";

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
    }
}
