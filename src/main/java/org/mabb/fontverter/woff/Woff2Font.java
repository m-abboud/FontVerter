/*
 * Copyright (C) Maddie Abboud 2016
 *
 * FontVerter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FontVerter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FontVerter. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mabb.fontverter.woff;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import com.aayushatharva.brotli4j.encoder.Encoder;
import org.apache.commons.lang3.ArrayUtils;
import org.mabb.fontverter.FontNotSupportedException;
import org.mabb.fontverter.FontProperties;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.FontVerterUtils;
import org.mabb.fontverter.converter.CombinedFontConverter;
import org.mabb.fontverter.converter.FontConverter;
import org.mabb.fontverter.converter.OtfToWoffConverter;
import org.mabb.fontverter.converter.WoffToOtfConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mabb.fontverter.woff.WoffConstants.TableFlagType.*;

public class Woff2Font extends WoffFont {
    private byte[] cachedCompressedBlock;

    public WoffTable createTable() {
        return new Woff2Font.Woff2Table(new byte[0], "arbitrary");
    }

    public void addFontTable(byte[] data, String tag, long checksum) {
        WoffTable table = new Woff2Table(data, tag);
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


        Brotli4jLoader.ensureAvailability();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);

        Encoder.Parameters params = new Encoder.Parameters();
        params.setMode(Encoder.Mode.TEXT);
        params.setQuality(11);
        params.setWindow(10);

        try (BrotliOutputStream brotliOutputStream = new BrotliOutputStream(outputStream, params)) {
            brotliOutputStream.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray();
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
        protected String tag = "";

        public Woff2Table(byte[] table, String tag) {
            super(table);
            this.tag = tag;
        }

        protected byte[] compress(byte[] bytes) throws IOException {
            // woff2 should run compress brotli on full data block not indivudal tables
            // except for special tables and transforms not = 0 which is still todo
            return bytes;
        }

        public byte[] getDirectoryData() throws IOException {
            WoffOutputStream writer = new WoffOutputStream();

            WoffConstants.TableFlagType flag = WoffConstants.TableFlagType.fromString(tag);
            writer.writeFlagByte(flag.getValue(), getTransform());

            if (flag == arbitrary) {
                // maybe should string pad < 4
                assert tag.length() == 4;
                writer.writeString(tag);
            }

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
            if (getFlag() == glyf || getFlag() == loca)
                return 3;
            return 0;
        }

        boolean isTableTransformed() {
            if (getFlag() == WoffConstants.TableFlagType.glyf || getFlag() == loca)
                return transform != 3;

            return transform != 0;
        }

        public WoffConstants.TableFlagType getFlag() {
            return WoffConstants.TableFlagType.fromString(tag);
        }

        public String getTag() {
            return getFlag().toString();
        }
    }
}
