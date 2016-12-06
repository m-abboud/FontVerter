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

public class WriteCvtTablePixelUnitsInstruction extends TtfInstruction {
    public int[] getCodeRanges() {
        return new int[]{0x44};
    }

    public void read(FontDataInputStream in) throws IOException {
    }

    public void execute(InstructionStack stack) throws IOException {
        float num = stack.popF26Dot6();
        Long index = stack.popUint32();

        // todo figure out type conversion to pixel units!
        vm.getGraphicsState().cvtValues.set(index.intValue(), (short) num);
    }
}
