package org.fontverter.woff;

import org.apache.commons.lang3.ArrayUtils;
import org.fontverter.FontVerterUtils;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.IOException;

import static org.fontverter.woff.WoffConstants.TableFlagType.*;
import static org.fontverter.woff.WoffConstants.TableFlagType.glyf;

public class Woff2Font extends WoffFont {
    private byte[] cachedCompressedBlock;

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
        if (cachedCompressedBlock == null)
            cachedCompressedBlock = brotliCompress(super.getCompressedDataBlock());

        return cachedCompressedBlock;
    }

    byte[] getRawData() throws IOException {
        byte[] bytes = super.getRawData();
        byte[] pad = FontVerterUtils.tablePaddingNeeded(bytes);
        bytes = ArrayUtils.addAll(bytes, pad);
        return bytes;
    }

    private byte[] brotliCompress(byte[] bytes) {
        BrotliLibraryLoader.loadBrotli();

        Brotli.Parameter param = new Brotli.Parameter(Brotli.Mode.TEXT, 100, 1, 0);
        BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(param);
        byte[] compressed = streamCompressor.compressArray(bytes, true);
        streamCompressor.close();

        return compressed;
    }

    public static class Woff2Table extends WoffTable {
        private int transform = -1;

        public Woff2Table(byte[] table, WoffConstants.TableFlagType flag) {
            super(table, flag);
//            tableData = padTableData(tableData);
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
