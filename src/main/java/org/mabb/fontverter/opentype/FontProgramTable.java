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

import org.mabb.fontverter.opentype.TtfInstructions.TtfInstructionParser;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FontProgramTable extends OpenTypeTable {
    private byte[] rawInstructions = new byte[0];
    private List<TtfInstruction> instructions = new LinkedList<TtfInstruction>();

    public String getTableType() {
        return "fpgm";
    }

    public void readData(byte[] data) throws IOException {
        rawInstructions = data;

        try {
            TtfInstructionParser parser = new TtfInstructionParser();
            instructions = parser.parse(rawInstructions);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected byte[] generateUnpaddedData() throws IOException {
        return rawInstructions;
    }

    public List<TtfInstruction> getInstructions() {
        return instructions;
    }
}
