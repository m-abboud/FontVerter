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

package org.mabb.fontverter.opentype.TtfInstructions;

import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;

import java.util.ArrayList;
import java.util.List;

public class TtfFunction {
    private List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();

    public List<TtfInstruction> getInstructions() {
        return instructions;
    }

    public void addInstruction(TtfInstruction instruction) {
        instructions.add(instruction);
    }
}
