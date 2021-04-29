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

import org.mabb.fontverter.*;
import org.mabb.fontverter.converter.CombinedFontConverter;
import org.mabb.fontverter.converter.FontConverter;
import org.mabb.fontverter.converter.OtfToWoffConverter.OtfToWoff2Converter;
import org.mabb.fontverter.converter.WoffToOtfConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import static org.mabb.fontverter.woff.Woff1Font.Woff1Table.WOFF1_TABLE_DIRECTORY_ENTRY_SIZE;

public class Woff1Font extends WoffFont {
	
    static final int WOFF1_HEADER_SIZE = 44;

    public WoffTable createTable() {
        return new Woff1Table(new byte[0], "    ");
    }

    public void addFontTable(byte[] data, String tag, long checksum) {
        Woff1Table table = new Woff1Table(data, tag);
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

    public void read(byte[] fontFile) throws IOException {
        WoffParser parser = new WoffParser();
        parser.parse(fontFile, this);
    }

    int tableDirectoryOffsetStart() {
        return tables.size() * WOFF1_TABLE_DIRECTORY_ENTRY_SIZE + WOFF1_HEADER_SIZE;
    }

    public boolean detectFormat(byte[] fontFile) {
        return FontVerterUtils.bytesStartsWith(fontFile, "wOFF");
    }

    public FontProperties getProperties() {
        FontProperties properties = new FontProperties();
        properties.setMimeType("application/font-woff");
        properties.setFileEnding("woff");
        properties.setCssFontFaceFormat("woff");
        return properties;
    }


    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        if (fontFormat == FontVerter.FontFormat.OTF)
            return new WoffToOtfConverter();
        if (fontFormat == FontVerter.FontFormat.WOFF2)
            return new CombinedFontConverter(new WoffToOtfConverter(), new OtfToWoff2Converter());

        throw new FontNotSupportedException("Font conversion not supported");
    }

    public static class Woff1Table extends WoffTable {
        static final int WOFF1_TABLE_DIRECTORY_ENTRY_SIZE = 20;
        int offset;
        long checksum;
        String tag;

        public Woff1Table(byte[] table, String tag) {
            super(table);
            this.tag = tag;
        }

        public byte[] getCompressedData() throws IOException {
            return padTableData(super.getCompressedData());
        }

        public String getTag() {
            return tag;
        }

        protected byte[] compress(byte[] bytes) throws IOException {
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
        	
			try (DeflaterOutputStream compressStream = new DeflaterOutputStream(out)) {
				compressStream.write(bytes);
			}
			
			return out.toByteArray();
        }

        protected void readCompressedData(byte[] readData) throws IOException {
            if (readData.length == originalLength) {
                this.tableData = readData;
                return;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
			try (InflaterOutputStream compressStream = new InflaterOutputStream(out)) {
				compressStream.write(readData);
				tableData = out.toByteArray();
			}
        }

		public byte[] getDirectoryData() throws IOException {
			try (WoffOutputStream writer = new WoffOutputStream()) {

				writer.writeString(tag);
				writer.writeInt(offset);
				writer.writeInt(getCompressedData().length - paddingAdded);
				writer.writeInt(tableData.length);
				writer.writeUnsignedInt((int) checksum);

				return writer.toByteArray();
			}
		}

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }
}
