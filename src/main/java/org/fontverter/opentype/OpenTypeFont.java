package org.fontverter.opentype;

import org.fontverter.FontWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OpenTypeFont {
    public static final int SFNT_HEADER_SIZE = 12;

    public List<OpenTypeTable> tables;
    public HeadTable head;
    public HorizontalHeadTable hhea;
    public MaximumProfileTable mxap;
    public HorizontalMetricsTable hmtx;
    public OS2WindowsMetricsTable os2;
    public PostScriptTable post;
    public CmapTable cmap;
    public NameTable name;

    private static Logger log = LoggerFactory.getLogger(OpenTypeFont.class);

    public static OpenTypeFont createBlankFont() {
        OpenTypeFont font = new OpenTypeFont();
        font.head = font.initTable(HeadTable.createEmptyTable());

        font.os2 = font.initTable(OS2WindowsMetricsTable.createEmptyTable());
        font.hhea = font.initTable(HorizontalHeadTable.createEmptyTable());
        font.mxap = font.initTable(MaximumProfileTable.createEmptyTable());

        font.post = font.initTable(PostScriptTable.createEmptyTable());
        font.cmap = font.initTable(CmapTable.createEmptyTable());
        font.hmtx = font.initTable(HorizontalMetricsTable.createEmptyTable(font));

        font.name = font.initTable(NameTable.createTable());
        font.normalizeTables();
        return font;
    }

    protected void normalizeTables() {
        mxap.setNumGlyphs(cmap.getNumberOfGlyphs());
        mxap.setNumGlyphs(cmap.getNumberOfGlyphs());
    }

    public OpenTypeFont() {
        tables = new ArrayList<OpenTypeTable>();
    }

    private <T extends OpenTypeTable> T initTable(T table) {
        tables.add(table);
        return table;
    }

    public void addTable(OpenTypeTable table) {
        tables.add(table);
    }

    public List<OpenTypeTable> getTables() {
        // OpenType spec says tables must be sorted alphabetically, but then later states some very specific order
        // then still needs to be added here
        Collections.sort(tables, new Comparator<OpenTypeTable>() {
            public int compare(OpenTypeTable left, OpenTypeTable right) {
                return left.getName().compareTo(right.getName());
            }
        });

        return tables;
    }

    public byte[] getFontData() throws IOException, FontSerializerException {
        calculateOffsets(tables);

        // gotta calc checksums before doing final full font checksum
        for (OpenTypeTable tableOn : tables)
            tableOn.finalizeRecord();

        // head checksum has to be very last after other checksums + offsets calculated so just grab full byte
        // output to calc instead of trying to re-edit the byte array at the right place
        head.checksumAdjustment(getRawData());
        // now we for realsies write out the font bytes
        return getRawData();
    }

    private byte[] getRawData() throws IOException, FontSerializerException {
        FontWriter out = FontWriter.createWriter();
        out.write(createSfntHeader(tables));

        for (OpenTypeTable tableOn : tables)
            out.write(tableOn.getRecordEntry());

        for (OpenTypeTable tableOn : tables)
            out.write(tableOn.getData());

        return out.toByteArray();
    }

    private void calculateOffsets(List<OpenTypeTable> tables) throws IOException, FontSerializerException {
        // must calculate table record offsets before we write any table data
        // start data offsets after sfnt header and table records
        int offset = tables.size() * OpenTypeTable.TABLE_RECORD_SIZE + OpenTypeFont.SFNT_HEADER_SIZE;
        for (OpenTypeTable tableOn : tables) {
            tableOn.setOffset(offset);
            log.debug("{} Offset Calc: {}", tableOn.getName(), offset);

            offset += tableOn.getData().length;
        }
    }

    private byte[] createSfntHeader(List<OpenTypeTable> tables) throws IOException {
        FontWriter out = FontWriter.createWriter();

        int numTables = tables.size();
        int searchRange = closestMaxPowerOfTwo(numTables) * 16;
        int entrySelector = (int) log2(closestMaxPowerOfTwo(numTables));
        int rangeShift = numTables * 16 - searchRange;

        out.write("OTTO".getBytes(FontWriter.openTypeCharEncoding));
        out.writeShort(numTables);
        out.writeShort(searchRange);
        out.writeShort(entrySelector);
        out.writeShort(rangeShift);
        return out.toByteArray();
    }

    private int closestMaxPowerOfTwo(double number) {
        int powerOfTwo = 1;
        while (powerOfTwo * 2 < number) {
            powerOfTwo = powerOfTwo * 2;
        }
        return powerOfTwo;
    }

    private double log2(int number) {
        return Math.log(number) / Math.log(2);
    }
}
