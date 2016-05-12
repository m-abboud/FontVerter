package org.fontverter.woff;

import org.fontverter.FontVerterUtils;
import org.fontverter.io.ByteDataOutputStream;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.IOException;

import static org.fontverter.woff.WoffConstants.TableFlagType.*;
import static org.fontverter.woff.WoffConstants.TableFlagType.glyf;

public class Woff2Font extends WoffFont {
    public WoffTable createTable() {
        return new Woff2Font.Woff2Table(new byte[0], arbitrary);
    }

    public void addFontTable(byte[] data, WoffConstants.TableFlagType flag, long checksum) {
        WoffTable table = new Woff2Table(data, flag);
        tables.add(table);
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "wOF2");
    }

    byte[] getCompressedDataBlock() throws IOException {
        return brotliCompress(super.getCompressedDataBlock());
    }

    private byte[] brotliCompress(byte[] bytes) {
        BrotliLibraryLoader.loadBrotli();
        BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(Brotli.DEFAULT_PARAMETER);

        return streamCompressor.compressArray(bytes, true);
    }

    public static class Woff2Table extends WoffTable {
        private int transform = -1;

        public Woff2Table(byte[] table, WoffConstants.TableFlagType flag) {
            super(table, flag);
        }

        protected byte[] compress(byte[] bytes) throws IOException {
            // woff2 should run compress brotli on full data block not indivudal tables
            // except for special tables and transforms not = 0 which is still todo
            return bytes;
        }

        public byte[] getDirectoryData() throws IOException {
            WoffOutputStream writer = new WoffOutputStream();

            writer.writeFlagByte(flag.getValue(), getTransform());
            // todo tag here for arbitrary flag type
            writer.writeUIntBase128(tableData.length);

            if (isTableTransformed())
                writer.writeUIntBase128(getCompressedData().length);

            return writer.toByteArray();
        }

        public int getTransformedLength() throws IOException {
            if (isTableTransformed())
                return getCompressedData().length;
            return tableData.length;
        }

        public void setTransform(int transform) {
            this.transform = transform;
        }

        public int getTransform() {
            if (transform == -1)
                transform = initTransformValue();

            return transform;
        }

        private int initTransformValue() {
            if (flag == glyf || flag == loca)
                return 3;
            return 0;
        }

        boolean isTableTransformed() {
            if (flag == WoffConstants.TableFlagType.glyf || flag == WoffConstants.TableFlagType.loca)
                return transform != 3;

            return transform != 0;
        }
    }
}
