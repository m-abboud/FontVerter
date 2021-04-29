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

package org.mabb.fontverter.converter;

import org.apache.commons.lang3.StringUtils;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OpenTypeTable;
import org.mabb.fontverter.woff.WoffFont;
import org.mabb.fontverter.woff.WoffTable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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

    private void readTables() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        for (WoffTable tableOn : woffFont.getTables()) {
            OpenTypeTable.OtfTableRecord record = new OpenTypeTable.OtfTableRecord();

            record.recordName = tableOn.getTag();
            if (record.recordName.length() < 4)
                record.recordName = record.recordName + StringUtils.repeat(" ", 4 - record.recordName.length());
            record.originalData = tableOn.getTableData();

            OpenTypeTable table = OpenTypeTable.createFromRecord(record, otfFont);
            table.isFromParsedFont = true;

            otfFont.addTable(table);
        }

        // have to order by dependant tables before doing table reads
        otfFont.orderTablesByDependencies();
        for (OpenTypeTable tableOn : otfFont.getTables())
            tableOn.readData(tableOn.record.originalData);

    }
}
