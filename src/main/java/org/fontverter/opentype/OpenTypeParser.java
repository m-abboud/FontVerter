package org.fontverter.opentype;

import org.fontverter.io.ByteDataInputStream;
import org.fontverter.io.DataTypeBindingDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.fontverter.opentype.OpenTypeTable.*;

public class OpenTypeParser {
    private OpenTypeFont font;
    private ByteDataInputStream input;

    private static final Logger log = LoggerFactory.getLogger(OpenTypeFont.class);

    public OpenTypeFont parse(byte[] data) throws IOException, InstantiationException, IllegalAccessException {
        this.input = new ByteDataInputStream(data);

        font = new OpenTypeFont();

        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        // read header first to figure out what woff font object type we need to create
        font.sfntHeader = (OpenTypeFont.SfntHeader) deserializer.deserialize(this.input, OpenTypeFont.SfntHeader.class);

        readTableHeaderEntries();
        readTableDataEntries();

        return font;
    }

    private void readTableHeaderEntries()
            throws IllegalAccessException, InstantiationException, IOException {
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();

        for (int i = 0; i < font.sfntHeader.numTables; i++) {
            OtfTableRecord record =
                    (OtfTableRecord) deserializer.deserialize(input, OtfTableRecord.class);

            OpenTypeTable table = createFromRecord(record);
            font.tables.add(table);
        }
    }

    private void readTableDataEntries() throws IOException {
        for (OpenTypeTable tableOn : font.tables) {
            input.seek(tableOn.getOffset());
            int dataReadLength = (int) tableOn.record.length;
            byte[] tableData = input.readBytes(dataReadLength);

            tableOn.readData(tableData);
        }
    }
}
