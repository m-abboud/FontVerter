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


import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.io.FontDataInputStream;

import java.util.Arrays;

public class TestCmapSubTable4 {
    @Test
    public void givenSubTableWithIdGreaterThanCharCode_whenCalcSegments_thenIdRangeOffsetNot0() throws Exception {
        Format4SubTable table = new Format4SubTable();
        table.addGlyphMapping(55, 1);
        // force calc segments
        table.getData();

        Assert.assertEquals(2, table.idRangeOffsets.get(0).intValue());
    }

    @Test
    public void givenSubTableWithIdGreaterThanCharCode_whenReadt0() throws Exception {
        Format4SubTable table = new Format4SubTable();
        table.addGlyphMapping(55, 1);
        // force calc segments

        byte[] data = table.getData();
        data = Arrays.copyOfRange(data, 2, data.length);

        Format4SubTable readTable = new Format4SubTable();
        readTable.readData(new FontDataInputStream(data));
        Assert.assertEquals(2, table.idRangeOffsets.get(0).intValue());
    }
}
