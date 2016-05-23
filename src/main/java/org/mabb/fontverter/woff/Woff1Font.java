package org.mabb.fontverter.woff;

import org.mabb.fontverter.FontVerterUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

import static org.mabb.fontverter.woff.Woff1Font.Woff1Table.WOFF1_TABLE_DIRECTORY_ENTRY_SIZE;


public class Woff1Font extends WoffFont {
    static final int WOFF1_HEADER_SIZE = 44;

    Woff1Font() {
    }

    public WoffTable createTable() {
        return new Woff1Table(new byte[0], WoffConstants.TableFlagType.arbitrary);
    }

    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag, long checksum) {
        Woff1Table table = new Woff1Table(data, flag);
        table.checksum = checksum;
        tables.add(table);
    }

    byte[] getRawData() throws IOException {
        calculateOffsets();
        return super.getRawData();
    }

    private void calculateOffsets() throws IOException {
        // must calculate table record offsets before we write any table data
        // start data offsets after sfnt header and table records
        int offset = tableDirectoryOffsetStart();

        for (WoffTable table : tables) {
            Woff1Table tableOn = (Woff1Table) table;
            tableOn.setOffset(offset);
            offset += tableOn.getCompressedData().length;
        }
    }

    int tableDirectoryOffsetStart() {
        return tables.size() * WOFF1_TABLE_DIRECTORY_ENTRY_SIZE + WOFF1_HEADER_SIZE;
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "wOFF");
    }

    public static class Woff1Table extends WoffTable {
        static final int WOFF1_TABLE_DIRECTORY_ENTRY_SIZE = 20;
        int offset;
        long checksum;

        public Woff1Table(byte[] table, WoffConstants.TableFlagType flag) {
            super(table, flag);
        }

        public byte[] getCompressedData() throws IOException {
            return padTableData(super.getCompressedData());
        }

        protected byte[] compress(byte[] bytes) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflaterOutputStream compressStream = new DeflaterOutputStream(out);
            compressStream.write(bytes);
            compressStream.close();

            return out.toByteArray();
        }

        public byte[] getDirectoryData() throws IOException {
            WoffOutputStream writer = new WoffOutputStream();

            writer.writeString(flag.toString());
            writer.writeInt(offset);
            writer.writeInt(getCompressedData().length - paddingAdded);
            writer.writeInt(tableData.length);
            writer.writeUnsignedInt((int) checksum);

            return writer.toByteArray();
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }
}
