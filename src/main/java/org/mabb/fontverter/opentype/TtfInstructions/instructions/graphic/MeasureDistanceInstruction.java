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
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;

import java.io.IOException;

public class MeasureDistanceInstruction extends TtfInstruction {
    public int[] getCodeRanges() {
        return new int[]{0x49, 0x4A};
    }

    boolean gridFittedOutline = true;

    public void read(FontDataInputStream in) throws IOException {
        if (code == 0x4A)
            gridFittedOutline = false;

    }

    public void execute(InstructionStack stack) throws IOException {
//        Long pointId2 = stack.popUint32();
//        Long pointId1 = stack.popUint32();

        // todo graphics state to grab actual point values
        Float distance = 0.0f;
        stack.push(distance);
    }
}
