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

package org.mabb.fontverter.opentype.TtfInstructions.instructions;

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.opentype.TtfInstructions.InstructionStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DeltaC3Instruction extends TtfInstruction {
    public int[] getCodeRanges() {
        return new int[]{0x75};
    }

    public void read(FontDataInputStream in) throws IOException {
    }

    public void execute(FontDataInputStream in, InstructionStack stack) throws IOException {
        Long numExceptionPairs = stack.popUint32();

        List<Long[]> cvtEntryPairs = new ArrayList<Long[]>();
        for (long i = 0; i < numExceptionPairs; i++) {
            Long cvtEntryNum = stack.popUint32();
            Long exceptionNum = stack.popUint32();

            cvtEntryPairs.add(new Long[]{cvtEntryNum, exceptionNum});
        }

        // todo should manipulate CVT table somehow after pops
        // Spec The DELTAC3[] instruction is exactly the same as the DELTAC1 instruction
        // except for operating at pixel per em sizes beginning with the
    }
}
