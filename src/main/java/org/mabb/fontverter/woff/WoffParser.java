package org.mabb.fontverter.woff;

import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.woff.Woff1Font.Woff1Table;
import org.mabb.fontverter.woff.WoffConstants.TableFlagType;
import org.meteogroup.jbrotli.BrotliStreamDeCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/* todo seperate out to woff1 and woff2 classes */
public class WoffParser {
    WoffFont font;

    private FontDataInputStream input;
    private static final Logger log = LoggerFactory.getLogger(WoffParser.class);

    public WoffParser() {
    }

    public WoffFont parse(byte[] data) throws IOException {
        return parse(data, null);
    }

    public WoffFont parse(byte[] data, WoffFont readTo) throws IOException {
        this.font = readTo;
        this.input = new FontDataInputStream(data);
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();

        // read header first to figure out what woff font object type we need to create
        WoffHeader header = (WoffHeader) deserializer.deserialize(this.input, WoffHeader.class);
        if (!header.isSignatureValid())
            throw new IOException("Woff header signature not recognized");
        if (font == null)
            font = WoffFont.createBlankFont(header.getWoffSignatureVersion());

        font.header = header;

        parseTables();

        return font;
    }

    private void parseTables() throws IOException {
        for (int i = 0; i < font.header.numTables; i++)
            parseDirectoryEntry();

        // todo seperate out v2 class...
        if (font instanceof Woff1Font) {
            for (WoffTable tableOn : font.getTables())
                parseTableData((Woff1Table) tableOn);
        } else
            parseV2CompressedBlock();
    }

    private void parseV2CompressedBlock() throws IOException {
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

    private void parseTableData(Woff1Table tableOn) throws IOException {
        input.seek(tableOn.offset);
        byte[] compressedData = input.readBytes(tableOn.transformLength);
        tableOn.readCompressedData(compressedData);
    }

    private void parseDirectoryEntry() throws IOException {
        WoffTable table = font.createTable();
        if (table instanceof Woff1Table)
            parseV1DirectoryEntry((Woff1Table) table);
        if (table instanceof Woff2Font.Woff2Table)
            parseV2DirectoryEntry((Woff2Font.Woff2Table) table);

        font.getTables().add(table);
    }

    private void parseV1DirectoryEntry(Woff1Table table) throws IOException {
        table.tag = input.readString(4);

        table.offset = input.readInt();
        table.transformLength = input.readInt();
        table.originalLength = input.readInt();
        table.checksum = input.readUnsignedInt();
    }

    private void parseV2DirectoryEntry(Woff2Font.Woff2Table table) throws IOException {
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
    }

    private boolean isNextUInt128() throws IOException {
        try {
            input.mark(5);
            input.readUIntBase128();
            input.reset();
            return true;
        } catch (IOException ex) {
            input.reset();
        }
        return false;
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
