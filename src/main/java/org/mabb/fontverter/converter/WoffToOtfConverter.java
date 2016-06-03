package org.mabb.fontverter.converter;

import org.apache.commons.lang3.StringUtils;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontConverter;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OpenTypeTable;
import org.mabb.fontverter.woff.WoffFont;
import org.mabb.fontverter.woff.WoffTable;

import java.io.IOException;

public class WoffToOtfConverter implements FontConverter {
    OpenTypeFont otfFont;
    WoffFont woffFont;

    public FVFont convertFont(FVFont font) throws IOException {
        this.woffFont = (WoffFont) font;

        otfFont = new OpenTypeFont();
        try {
            readTables();
        } catch (Exception e) {
            throw new IOException(e);
        }
        otfFont.finalizeFont();

        return otfFont;
    }

    private void readTables() throws IOException, InstantiationException, IllegalAccessException {
        for (WoffTable tableOn : woffFont.getTables()) {
            OpenTypeTable.OtfTableRecord record = new OpenTypeTable.OtfTableRecord();

            record.recordName = tableOn.getTag();
            if (record.recordName.length() < 4)
                record.recordName = record.recordName + StringUtils.repeat(" ", 4 - record.recordName.length());

            OpenTypeTable table = OpenTypeTable.createFromRecord(record, otfFont);
            table.readData(tableOn.getTableData());
            table.isFromParsedFont = true;

            otfFont.addTable(table);
        }
    }
}
