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

package org.mabb.fontverter.opentype.TtfInstructions.instructions.graphic;

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.opentype.TtfInstructions.InstructionStack;
import org.mabb.fontverter.opentype.TtfInstructions.TtfGraphicsState;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;

import java.io.IOException;

public class FreedomAndProjectToAxisInstruction extends TtfInstruction {
    public int[] getCodeRanges() {
        return new int[]{0x00, 0x01};
    }

    public boolean isXAxis = false;

    public void read(FontDataInputStream in) throws IOException {
        if (code == 0x01)
            isXAxis = true;
    }

    public void execute(FontDataInputStream in, InstructionStack stack) throws IOException {
        TtfGraphicsState state = vm.getGraphicsState();
        if (isXAxis)
            state.projectionVector.x = state.freedomVector.x = 0;
        else
            state.projectionVector.y = state.freedomVector.y = 0;
    }
}
