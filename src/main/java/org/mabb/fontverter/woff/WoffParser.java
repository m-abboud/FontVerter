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

public class WoffParser {
    protected WoffFont font;
    protected FontDataInputStream input;

    public WoffFont parse(byte[] data) throws IOException {
        return parse(data, null);
    }

    public WoffFont parse(byte[] data, WoffFont readTo) throws IOException {
        this.font = readTo;
        initalizeFont();

        this.input = new FontDataInputStream(data);
        font.header = parseHeader();
        parseTables();

        return font;
    }

    protected void initalizeFont() {
        if (font == null)
            font = WoffFont.createBlankFont(1);
    }

    private WoffHeader parseHeader() throws IOException {
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();

        WoffHeader header = (WoffHeader) deserializer.deserialize(this.input, WoffHeader.class);
        if (!header.isSignatureValid())
            throw new IOException("Woff header signature not recognized");
        return header;
    }

    protected void parseTables() throws IOException {
        for (int i = 0; i < font.header.numTables; i++)
            parseDirectoryEntry();

        for (WoffTable tableOn : font.getTables())
            parseTableData((Woff1Table) tableOn);
    }

    private void parseTableData(Woff1Table tableOn) throws IOException {
        input.seek(tableOn.offset);
        byte[] compressedData = input.readBytes(tableOn.transformLength);

        tableOn.readCompressedData(compressedData);
    }

    private void parseDirectoryEntry() throws IOException {
        Woff1Table table = (Woff1Table) font.createTable();

        table.tag = input.readString(4);
        table.offset = input.readInt();
        table.transformLength = input.readInt();
        table.originalLength = input.readInt();
        table.checksum = input.readUnsignedInt();

        font.getTables().add(table);
    }
}
