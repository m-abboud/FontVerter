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

public class GetInfoInstruction extends TtfInstruction {
    public int[] getCodeRanges() {
        return new int[]{0x88};
    }

    public void read(FontDataInputStream in) throws IOException {
    }

    public void execute(FontDataInputStream in, InstructionStack stack) throws IOException {
        Long infoCode = stack.popUint32();

        Long infoResult = 0L;
        if (isBitSet(0, infoCode))
            infoResult = infoResult | getEngineVersion();
        if (isBitSet(1, infoCode))
            infoResult = infoResult | (isRotated() << 8);
        else if (isBitSet(2, infoCode))
            infoResult = infoResult | (isStretched() << 9);

        stack.push(infoResult);
    }

    private Long getEngineVersion() {
        // TTf spec says:
        // System Engine Version
        // Macintosh System 6.0 = 1
        // Macintosh System 7.0 = 2
        // Windows 3.1 = 3
        // KanjiTalk 6.1 = 4
        // so I'm taking any windows = 3 and any mac = 2
        if (isWindows())
            return 3L;

        return 2L;
    }

    private int isStretched() {
        return 0;
    }

    private int isRotated() {
        return 0;
    }

    private boolean isBitSet(int position, Long num) {
        return ((num >> position) & 1) == 1;
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
