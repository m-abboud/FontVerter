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
import org.mabb.fontverter.opentype.TtfInstructions.TtfGraphicsState.ScanDropoutMode;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;

import java.io.IOException;
import java.util.List;

import static org.mabb.fontverter.FontVerterUtils.isBitSet;
import static org.mabb.fontverter.opentype.TtfInstructions.TtfGraphicsState.ScanDropoutMode.*;

public class ScanConversionControlInstruction extends TtfInstruction {
    public int[] getCodeRanges() {
        return new int[]{0x85};
    }

    public void read(FontDataInputStream in) throws IOException {
    }

    public void execute(InstructionStack stack) throws IOException {
        long flags = stack.popNumber().longValue();

        List<ScanDropoutMode> modes = vm.getGraphicsState().dropoutControlModes;
        modes.clear();

        if (isBitSet(8, flags))
            modes.add(TRUE_IF_PPEM_LESS_THAN_THRESHOLD);
        if (isBitSet(9, flags))
            modes.add(TRUE_IF_GLYPH_IS_ROTATED);
        if (isBitSet(10, flags))
            modes.add(TRUE_IF_GLYPH_STRETCHED);

        if (isBitSet(11, flags))
            modes.add(FALSE_UNLESS_PPEM_LESS_THAN_THRESHOLD);
        if (isBitSet(12, flags))
            modes.add(FALSE_UNLESS_STRETCHED);
        if (isBitSet(13, flags))
            modes.add(FALSE_UNLESS_STRETCHED);

        vm.getGraphicsState().droputThreshold = (flags & 0xFF);
    }
}
