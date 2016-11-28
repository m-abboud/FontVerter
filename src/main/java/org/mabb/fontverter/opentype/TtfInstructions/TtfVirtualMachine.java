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
import org.mabb.fontverter.opentype.TtfInstructions.instructions.ElseInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.EndIfInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.IfInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class TtfVirtualMachine {
    private InstructionStack stack;
    private final FontDataInputStream fontInput;
    private Stack<IfInstruction> ifStack = new Stack<IfInstruction>();

    public TtfVirtualMachine(FontDataInputStream fontInput) {
        this.fontInput = fontInput;
        this.stack = new InstructionStack();
    }

    public void execute(List<TtfInstruction> instructions) throws IOException {
        for (TtfInstruction instructionOn : instructions)
            execute(instructionOn);
    }

    public void execute(TtfInstruction instruction) throws IOException {
        if (instruction instanceof EndIfInstruction) {
            if (ifStack.size() == 0)
                throw new TtfVmRuntimeException("End If with no matching If!!");

            ifStack.pop();
            return;
        }

        if (instruction instanceof ElseInstruction) {
            if (ifStack.size() == 0)
                throw new TtfVmRuntimeException("Else with no matching If!!");

            ifStack.peek().shouldExecute = !ifStack.peek().shouldExecute;
        }

        if (!shouldExecuteBranch()) {
            if (instruction instanceof IfInstruction)
                ifStack.push((IfInstruction) instruction);
            return;
        }

        instruction.execute(fontInput, stack);

        if (instruction instanceof IfInstruction)
            ifStack.push((IfInstruction) instruction);
    }

    public boolean shouldExecuteBranch() {
        if (ifStack.size() == 0)
            return true;

        return ifStack.get(ifStack.size() - 1).shouldExecute;
    }

    InstructionStack getStack() {
        return stack;
    }

    public class TtfVmRuntimeException extends IOException {
        public TtfVmRuntimeException(String message) {
            super(message);
        }
    }
}
