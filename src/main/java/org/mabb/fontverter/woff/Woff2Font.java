package org.mabb.fontverter.woff;

import org.apache.commons.lang3.ArrayUtils;
import org.mabb.fontverter.*;
import org.mabb.fontverter.converter.OtfToWoffConverter;
import org.mabb.fontverter.converter.OtfToWoffConverter.OtfToWoff2Converter;
import org.mabb.fontverter.converter.WoffToOtfConverter;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;

import java.io.IOException;

import static org.mabb.fontverter.woff.WoffConstants.TableFlagType.arbitrary;
import static org.mabb.fontverter.woff.WoffConstants.TableFlagType.glyf;
import static org.mabb.fontverter.woff.WoffConstants.TableFlagType.loca;

public class Woff2Font extends WoffFont {
    private byte[] cachedCompressedBlock;

    public WoffTable createTable() {
        return new Woff2Font.Woff2Table(new byte[0], arbitrary);
    }

    public void addFontTable(byte[] data, String tag, long checksum) {
        WoffTable table = new Woff2Table(data, WoffConstants.TableFlagType.fromString(tag));
        tables.add(table);
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "wOF2");
    }

    public void read(byte[] fontFile) throws IOException {
        Woff2Parser parser = new Woff2Parser();
        parser.parse(fontFile, this);
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

    public FontProperties getProperties() {
        FontProperties properties = new FontProperties();
        properties.setMimeType("application/font-woff2");
        properties.setFileEnding("woff");
        properties.setCssFontFaceFormat("woff2");
        return properties;
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        if (fontFormat == FontVerter.FontFormat.OTF)
            return new WoffToOtfConverter();
        if (fontFormat == FontVerter.FontFormat.WOFF1)
            return new CombinedFontConverter(new WoffToOtfConverter(), new OtfToWoffConverter());

        throw new FontNotSupportedException("Font conversion not supported");
    }

    public static class Woff2Table extends WoffTable {
        private int transform = -1;
        protected WoffConstants.TableFlagType flag;

        public Woff2Table(byte[] table, WoffConstants.TableFlagType flag) {
            super(table);
            this.flag = flag;
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
            if (flag == WoffConstants.TableFlagType.glyf || flag == loca)
                return transform != 3;

            return transform != 0;
        }

        public WoffConstants.TableFlagType getFlag() {
            return flag;
        }

        public String getTag() {
            return flag.toString();
        }
    }
}
