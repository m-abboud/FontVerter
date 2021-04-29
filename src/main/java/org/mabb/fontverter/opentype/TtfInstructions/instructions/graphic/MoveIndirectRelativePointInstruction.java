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

import static org.mabb.fontverter.FontVerterUtils.isBitSet;

public class MoveIndirectRelativePointInstruction extends TtfInstruction {
    public int[] getCodeRanges() {
        return new int[]{0xE0, 0xFF};
    }

    boolean resetRp0 = false;
    boolean keepDistanceGreaterThanMin = false;
    boolean roundDistance = false;
    short engineDistanceType = 0;

    public void read(FontDataInputStream in) throws IOException {
        // 32 values between the instruction code range are params and fit into a byte
        byte flags = (byte) (code - 0xE0);
        resetRp0 = isBitSet(0, flags);
        keepDistanceGreaterThanMin = isBitSet(1, flags);
        roundDistance = isBitSet(2, flags);

        engineDistanceType = (short) (flags >> 3);
    }

    public void execute(InstructionStack stack) throws IOException {
//        Float cvtEntry = 
    	stack.popF26Dot6();
//        Long pointId = 
    	stack.popUint32();

        // todo graphics state handeling
    }
}
