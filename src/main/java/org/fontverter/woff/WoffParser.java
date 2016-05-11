package org.fontverter.woff;

import org.fontverter.io.ByteBindingDeserializer;
import org.fontverter.io.ByteDataInputStream;
import org.fontverter.woff.Woff1Font.Woff1Table;

import java.io.IOException;

public class WoffParser {
    WoffFont font;
    private ByteDataInputStream input;

    public WoffParser() {
    }

    public WoffFont parse(byte[] data) throws IOException {
        this.input = new ByteDataInputStream(data);
        ByteBindingDeserializer deserializer = new ByteBindingDeserializer();

        // read header first to figure out what woff font object type we need to create
        WoffHeader header = (WoffHeader) deserializer.deserialize(this.input, WoffHeader.class);
        if (!header.isSignatureValid())
            throw new IOException("Woff header signature not recognized");

        font = WoffFont.createBlankFont(header.getWoffSignatureVersion());
        font.header = header;

        parseTables();

        return font;
    }

    private void parseTables() throws IOException {
        for (int i = 0; i < font.header.numTables; i++)
            parseDirectoryEntry();

        for (WoffTable tableOn : font.getTables())
            parseTableData(tableOn);
    }

    private void parseTableData(WoffTable tableOn) throws IOException {
        if (tableOn instanceof Woff1Table) {
            input.seek(((Woff1Table) tableOn).offset);
        }
        tableOn.compressedData = input.readBytes(tableOn.compressedLength);
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
        String tag = input.readString(4);
        table.flag = WoffConstants.TableFlagType.fromString(tag);

        table.offset = input.readInt();
        table.compressedLength = input.readInt();
        table.originalLength = input.readInt();
        table.checksum = input.readUnsignedInt();
    }

    private void parseV2DirectoryEntry(Woff2Font.Woff2Table table) throws IOException {
        int tag = input.read();
        table.flag = WoffConstants.TableFlagType.fromInt(tag);

        // tag field is optional, so try read a Base128 and if it's invalid then
        // we assume the tag field is there and read it but discard result since flag has it already
        try {
            input.mark(5);
            input.readUIntBase128();
            input.reset();
        } catch (IOException ex) {
            input.reset();
            input.readInt();
        }

        table.originalLength = input.readUIntBase128();
        // fixme woff spec cryptically says only if applicable next to the transformed length field
        // dunno when not to read it, think it's 0 if not applicable  but dunno if it can ommited completley
        // ever
        table.compressedLength = input.readUIntBase128();
        if (table.compressedLength == 0)
            table.compressedLength = table.originalLength;
    }
}
