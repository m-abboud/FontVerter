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

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.FontDataOutputStream;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.mabb.fontverter.io.FontDataOutputStream.OPEN_TYPE_CHARSET;
import static org.slf4j.LoggerFactory.getLogger;

public class ControlValueTable extends OpenTypeTable {
    private static final Logger log = getLogger(ControlValueTable.class);
    private List<Short> values = new LinkedList<Short>();

    public String getTableType() {
        return "cvt ";
    }

    public void readData(byte[] data) throws IOException {
        FontDataInputStream input = new FontDataInputStream(data);
        while (input.available() >= 2)
            values.add(input.readShort());

        if (input.available() == 1) {
            log.info("original cvt table data length not divisble by two, adding 1 byte padding.");
            values.add((short) input.readByte());
        }
    }

    protected byte[] generateUnpaddedData() throws IOException {
        FontDataOutputStream out = new FontDataOutputStream(OPEN_TYPE_CHARSET);
        for (Short valueOn : values)
            out.writeShort(valueOn);

        return out.toByteArray();
    }

    public List<Short> getValues() {
        return values;
    }

    public Short getValue(Long index) throws CvtValueNotFoundException {
        if (index > values.size())
            throw new CvtValueNotFoundException();

        return values.get(index.intValue());
    }

    public static class CvtValueNotFoundException extends IOException {
    }
}
