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

import org.mabb.fontverter.woff.Woff2Font.Woff2Table;
import org.mabb.fontverter.woff.WoffConstants.TableFlagType;
import org.meteogroup.jbrotli.BrotliDeCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Woff2Parser extends WoffParser{
    private static final Logger log = LoggerFactory.getLogger(Woff2Parser.class);

    protected void initalizeFont() {
        if (font == null)
            font = WoffFont.createBlankFont(2);
    }

    @Override
    protected void parseTables() throws IOException {
        for (int i = 0; i < font.header.numTables; i++)
            parseDirectoryEntry();

        parseCompressedBlockTableData();
    }

    private void parseDirectoryEntry() throws IOException {
        Woff2Table table = (Woff2Table) font.createTable();

        int[] rawFlag = input.readSplitBits(2);

        table.setTransform(rawFlag[0]);
        table.flag = TableFlagType.fromInt(rawFlag[1]);

        if (table.flag.getValue() == 63) {
            String tagStr = new String(ByteBuffer.allocate(4).putInt(input.readInt()).array());
            log.error("!! arbitrary flag type not tested" + tagStr);
        }

        table.originalLength = input.readUIntBase128();

        // transformLength present IFF non null transform ie something before brotli compress
        if (table.isTableTransformed())
            table.transformLength = input.readUIntBase128();
        if (table.transformLength == 0)
            table.transformLength = table.originalLength;

        log.debug("Woff2 parse table dir read: {} {} o-len:" + table.originalLength + " t-len:" + table.transformLength,
                table.flag, table.getTransform());

        font.getTables().add(table);
    }

    private void parseCompressedBlockTableData() throws IOException {
        byte[] block = input.readBytes(font.header.totalCompressedSize);
        block = brotliDecompress(block);

        int offset = 0;
        for (WoffTable tableOn : font.getTables()) {
            try {
                int end = tableOn.transformLength + offset;
                if(end > block.length)
                    end = block.length;

                tableOn.tableData = Arrays.copyOfRange(block, offset, end);
                offset += tableOn.transformLength;
            } catch (Exception e) {
                return;
            }
        }
    }

    private byte[] brotliDecompress(byte[] compressed) {
        BrotliLibraryLoader.loadBrotli();
        byte[] decompressed = new byte[compressed.length * 4];
        int decompressLength = new BrotliDeCompressor().deCompress(compressed, decompressed);
        return Arrays.copyOfRange(decompressed, 0, decompressLength);
    }
}
