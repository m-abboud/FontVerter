package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OpenTypeParser {
    private static final Logger log = LoggerFactory.getLogger(OpenTypeFont.class);

    private OpenTypeFont font;
    private FontDataInputStream input;

    public OpenTypeFont parse(byte[] data) throws IOException, InstantiationException, IllegalAccessException {
        return parse(data, new OpenTypeFont());
    }

    public OpenTypeFont parse(byte[] data, OpenTypeFont font) throws IOException, InstantiationException, IllegalAccessException {
        this.font = font;
        this.input = new FontDataInputStream(data);

        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        // read header first to figure out what woff font object type we need to create
        font.sfntHeader = (SfntHeader) deserializer.deserialize(this.input, new SfntHeader());

        readTableHeaderEntries();
        readTableDataEntries();

        return font;
    }

    private void readTableHeaderEntries()
            throws IllegalAccessException, InstantiationException, IOException {
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();

        for (int i = 0; i < font.sfntHeader.numTables; i++) {
            OpenTypeTable.OtfTableRecord record =
                    (OpenTypeTable.OtfTableRecord) deserializer.deserialize(input, OpenTypeTable.OtfTableRecord.class);

            OpenTypeTable table = OpenTypeTable.createFromRecord(record, font);
            table.isFromParsedFont = true;
            font.addTable(table);
        }
    }

    private void readTableDataEntries() throws IOException {
        for (OpenTypeTable tableOn : font.getTables()) {
            input.seek((int) tableOn.getOffset());

            int dataReadLength = (int) tableOn.record.length;
            byte[] tableData = input.readBytes(dataReadLength);

            tableOn.readData(tableData);
        }
    }
}
