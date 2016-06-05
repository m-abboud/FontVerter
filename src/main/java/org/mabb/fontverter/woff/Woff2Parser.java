package org.mabb.fontverter.woff;

import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.woff.Woff2Font.Woff2Table;
import org.mabb.fontverter.woff.WoffConstants.TableFlagType;
import org.meteogroup.jbrotli.BrotliStreamDeCompressor;
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

    private byte[] brotliDecompress(byte[] bytes) {
        ByteBuffer inBuffer = ByteBuffer.allocate(bytes.length);
        ByteBuffer outBuffer = ByteBuffer.allocate(bytes.length * 4);
        inBuffer.put(bytes);
        inBuffer.limit(bytes.length);
        inBuffer.position(0);

        BrotliLibraryLoader.loadBrotli();
        BrotliStreamDeCompressor streamCompressor = new BrotliStreamDeCompressor();
        int decompressLength = streamCompressor.deCompress(inBuffer, outBuffer);

        byte[] outBytes = new byte[decompressLength];
        outBuffer.get(outBytes);
        return outBytes;
    }
}
