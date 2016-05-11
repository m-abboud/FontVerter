package org.fontverter.woff;

import org.fontverter.FontVerterUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

import static org.fontverter.woff.Woff1Font.Woff1FontTable.WOFF1_TABLE_DIRECTORY_ENTRY_SIZE;


public class Woff1Font extends WoffFont {
    private static final int WOFF1_HEADER_SIZE = 44;

    Woff1Font() {
    }

    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag, long checksum) {
        Woff1FontTable table = new Woff1FontTable(data, flag);
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
        int offset = tables.size() * WOFF1_TABLE_DIRECTORY_ENTRY_SIZE + WOFF1_HEADER_SIZE;
        for (FontTable table : tables) {
            Woff1FontTable tableOn = (Woff1FontTable) table;
            tableOn.setOffset(offset);
            offset += tableOn.getCompressedTableData().length;
        }
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "wOFF");
    }

    public static class Woff1FontTable extends FontTable {
        static final int WOFF1_TABLE_DIRECTORY_ENTRY_SIZE = 20;

        private int offset;
        long checksum;

        public Woff1FontTable(byte[] table, WoffConstants.TableFlagType flag) {
            super(table, flag);
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
            writer.writeInt(getCompressedTableData().length - paddingAdded);
            writer.writeInt(tableData.length);
            writer.writeUnsignedInt((int) checksum);

            return writer.toByteArray();
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }
}
