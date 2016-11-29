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
import org.mabb.fontverter.opentype.TtfInstructions.TtfInstructionVisitor;
import org.mabb.fontverter.opentype.TtfInstructions.TtfVirtualMachine;

import java.io.IOException;

public abstract class TtfInstruction {
    public int code;
    public TtfVirtualMachine vm;

    public abstract int[] getCodeRanges();

    public abstract void read(FontDataInputStream in) throws IOException;

    public abstract void execute(FontDataInputStream in, InstructionStack stack) throws IOException;

    public void accept(TtfInstructionVisitor visitor) throws IOException {
        visitor.visitGeneric(this);
    }

    public boolean doesMatch(int code) {
        int[] range = getCodeRanges();
        if (getCodeRanges().length == 1)
            return code == range[0];
        else
            return code >= range[0] && code <= range[1];
    }

    protected static Long boolToUint32(boolean value) {
        Long uIntResult = 0L;
        if (value)
            uIntResult = 1L;

        return uIntResult;
    }
}
