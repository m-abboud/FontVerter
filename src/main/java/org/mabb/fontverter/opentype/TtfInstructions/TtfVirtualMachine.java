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

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;

import java.io.IOException;
import java.util.List;

public class TtfVirtualMachine {
    private InstructionStack stack;
    private final FontDataInputStream fontInput;

    public TtfVirtualMachine(FontDataInputStream fontInput) {
        this.fontInput = fontInput;
        this.stack = new InstructionStack();
    }

    public void execute(List<TtfInstruction> instructions) throws IOException {
        for (TtfInstruction instructionOn : instructions)
            execute(instructionOn);
    }

    public void execute(TtfInstruction instruction) throws IOException {
        instruction.execute(fontInput, stack);
    }

    InstructionStack getStack() {
        return stack;
    }
}
