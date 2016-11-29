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

import org.mabb.fontverter.opentype.TtfInstructions.instructions.*;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.IfInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.ElseInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.EndFunctionInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.EndIfInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.FunctionDefInstruction;

import java.io.IOException;

public interface TtfInstructionVisitor {
    void visitGeneric(TtfInstruction instruction) throws IOException;

    void visit(IfInstruction instruction) throws IOException;

    void visit(ElseInstruction instruction) throws IOException;

    void visit(EndIfInstruction instruction) throws IOException;

    void visit(FunctionDefInstruction instruction) throws IOException;

    void visit(EndFunctionInstruction instruction) throws IOException;
}
