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

package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.FontDataInput;
import org.mabb.fontverter.opentype.GlyphMapReader.GlyphMapping;
import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.FontDataOutputStream;

import java.io.IOException;
import java.util.*;

import static org.mabb.fontverter.opentype.OtfNameConstants.*;

abstract class CmapSubTable {
    public static final int CMAP_RECORD_BYTE_SIZE = 8;

    protected int formatNumber;
    protected long languageId = 0;
    protected byte[] rawReadData;

    private int platformId;
    private int encodingId;
    private long subTableOffset;

    public long getSubTableOffset() {
        return subTableOffset;
    }

    long getLanguageId() {
        return languageId;
    }

    public void setSubTableOffset(long subTableOffset) {
        this.subTableOffset = subTableOffset;
    }

    public byte[] getRecordData() throws IOException {
		try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {
			writer.writeUnsignedShort(platformId);
			writer.writeUnsignedShort(encodingId);
			writer.writeUnsignedInt((int) subTableOffset);
			
			return writer.toByteArray();
		}
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public OtfEncodingType getEncodingType() {
        return OtfEncodingType.fromInt(encodingId);
    }

    public void setEncodingId(int encodingId) {
        this.encodingId = encodingId;
    }

    public abstract byte[] getData() throws IOException;

    public abstract int glyphCount();

    public abstract void readData(FontDataInput input) throws IOException;

    public List<GlyphMapping> getGlyphMappings() {
        return new ArrayList<GlyphMapping>();
    }

    protected static class Format0SubTable extends CmapSubTable {
        private static final int FORMAT0_HEADER_SIZE = 6 + 256;
        // LinkedHashMap important, for keeping ordering the same for loops
        private LinkedHashMap<Integer, Integer> charCodeToGlyphId = new LinkedHashMap<Integer, Integer>();

        public Format0SubTable() {
            formatNumber = 0;
            for (int i = 0; i < 256; i++)
                charCodeToGlyphId.put(i, 0);
        }

        @Override
        public byte[] getData() throws IOException {
			try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {

				// kludge for read otf fonts
				if (rawReadData != null) {
					writer.writeUnsignedShort(formatNumber);
					writer.writeUnsignedShort(rawReadData.length + 4);
					writer.write(rawReadData);

					return writer.toByteArray();
				}

				writer.writeUnsignedShort((int) formatNumber);
				writer.writeUnsignedShort(getLength());
				writer.writeUnsignedShort((int) getLanguageId());

				for (Map.Entry<Integer, Integer> entry : charCodeToGlyphId.entrySet()) {
					writer.writeByte(entry.getValue());
				}

				return writer.toByteArray();
			}
        }

        public int glyphCount() {
            return 0;
        }

        private int getLength() {
            // + 1 to size for appearant padding need
            return FORMAT0_HEADER_SIZE;
        }

        public void addGlyphMapping(int characterCode, int glyphId) {
            charCodeToGlyphId.put(characterCode, glyphId);
        }

        @SuppressWarnings("resource")
		public void readData(FontDataInput input) throws IOException {
            int length = input.readUnsignedShort();
            rawReadData = input.readBytes(length - 4);
            input = new FontDataInputStream(rawReadData);
        }
    }

    static class Format2SubTable extends CmapSubTable {
        public Format2SubTable() {
            formatNumber = 2;
        }

        public byte[] getData() throws IOException {
            // kludge for read otf fonts
			try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {
				writer.writeUnsignedShort(formatNumber);
				writer.writeUnsignedShort(rawReadData.length + 4);
				writer.write(rawReadData);

				return writer.toByteArray();
			}
        }

        public int glyphCount() {
            return 0;
        }

        @SuppressWarnings("resource")
		public void readData(FontDataInput input) throws IOException {
            int length = input.readUnsignedShort();
            rawReadData = input.readBytes(length - 4);
            input = new FontDataInputStream(rawReadData);

        }
    }

    static class Format6SubTable extends CmapSubTable {
        public Format6SubTable() {
            formatNumber = 6;
        }

        public byte[] getData() throws IOException {
            // kludge for read otf fonts
			try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {
				writer.writeUnsignedShort(formatNumber);
				writer.writeUnsignedShort(rawReadData.length + 4);
				writer.write(rawReadData);

				return writer.toByteArray();
			}
        }

        public int glyphCount() {
            return 0;
        }

        @SuppressWarnings("resource")
		public void readData(FontDataInput input) throws IOException {
            int length = input.readUnsignedShort();
            rawReadData = input.readBytes(length - 4);
            input = new FontDataInputStream(rawReadData);

        }
    }

    static class Format8SubTable extends CmapSubTable {
        public Format8SubTable() {
            formatNumber = 8;
        }

        public byte[] getData() throws IOException {
            // kludge for read otf fonts
			try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {
				writer.writeUnsignedShort(formatNumber);
				// reserved
				writer.writeUnsignedShort(0);
				writer.writeUnsignedInt(rawReadData.length + 8);
				writer.write(rawReadData);

				return writer.toByteArray();
			}
        }

        public int glyphCount() {
            return 0;
        }

        @SuppressWarnings("resource")
		public void readData(FontDataInput input) throws IOException {
//            int reserved = 
        	input.readUnsignedShort();
            long length = input.readUnsignedInt();
            rawReadData = input.readBytes((int) (length - 8));
            input = new FontDataInputStream(rawReadData);
            languageId = input.readUnsignedInt();
        }
    }

    static class Format10SubTable extends CmapSubTable {
        public Format10SubTable() {
            formatNumber = 10;
        }

        public byte[] getData() throws IOException {
            // kludge for read otf fonts
			try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {
				writer.writeUnsignedShort(formatNumber);
				// reserved
				writer.writeUnsignedShort(0);
				writer.writeUnsignedInt(rawReadData.length + 8);
				writer.write(rawReadData);

				return writer.toByteArray();
			}
        }

        public int glyphCount() {
            return 0;
        }

        @SuppressWarnings("resource")
		public void readData(FontDataInput input) throws IOException {
            // int reserved = 
        	input.readUnsignedShort();
            long length = input.readUnsignedInt();
            rawReadData = input.readBytes((int) (length - 8));
            input = new FontDataInputStream(rawReadData);
            languageId = input.readUnsignedInt();
        }
    }

    static class Format12SubTable extends CmapSubTable {
        public Format12SubTable() {
            formatNumber = 12;
        }

        public byte[] getData() throws IOException {
            // kludge for read otf fonts
			try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {
				writer.writeUnsignedShort(formatNumber);
				// reserved
				writer.writeUnsignedShort(0);
				writer.writeUnsignedInt(rawReadData.length + 8);
				writer.write(rawReadData);

				return writer.toByteArray();
			}
        }

        public int glyphCount() {
            return 0;
        }

        @SuppressWarnings("resource")
		public void readData(FontDataInput input) throws IOException {
            // int reserved = 
        	input.readUnsignedShort();
            long length = input.readUnsignedInt();
            rawReadData = input.readBytes((int) (length - 8));
            input = new FontDataInputStream(rawReadData);

            languageId = input.readUnsignedInt();
        }
    }

    static class Format13SubTable extends CmapSubTable {
        public Format13SubTable() {
            formatNumber = 13;
        }

        public byte[] getData() throws IOException {
            // kludge for read otf fonts
			try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {
				writer.writeUnsignedShort(formatNumber);
				// reserved
				writer.writeUnsignedShort(0);
				writer.writeUnsignedInt(rawReadData.length + 8);
				writer.write(rawReadData);

				return writer.toByteArray();
			}
        }

        public int glyphCount() {
            return 0;
        }

        @SuppressWarnings("resource")
		public void readData(FontDataInput input) throws IOException {
            // int reserved = 
        	input.readUnsignedShort();
            long length = input.readUnsignedInt();
            rawReadData = input.readBytes((int) (length - 8));
            input = new FontDataInputStream(rawReadData);

            languageId = input.readUnsignedInt();
        }
    }

    static class Format14SubTable extends CmapSubTable {
        public Format14SubTable() {
            formatNumber = 14;
        }

        public byte[] getData() throws IOException {
            // kludge for read otf fonts
			try (FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET)) {
				writer.writeUnsignedShort(formatNumber);
				writer.writeUnsignedInt(rawReadData.length + 6);
				writer.write(rawReadData);

				return writer.toByteArray();
			}
        }

        public int glyphCount() {
            return 0;
        }

        @SuppressWarnings("resource")
		public void readData(FontDataInput input) throws IOException {
            long length = input.readUnsignedInt();
            rawReadData = input.readBytes((int) (length - 6));
            input = new FontDataInputStream(rawReadData);

            languageId = input.readUnsignedInt();
        }
    }
}
