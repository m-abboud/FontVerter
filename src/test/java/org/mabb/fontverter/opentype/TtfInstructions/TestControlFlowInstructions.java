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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.*;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.*;

import java.util.ArrayList;
import java.util.List;

public class TestControlFlowInstructions {
    private TtfInstructionParser parser;
    private TtfVirtualMachine vm;

    @Before
    public void init() {
        parser = new TtfInstructionParser();
        vm = new TtfVirtualMachine(new FontDataInputStream(new byte[0]));
    }

    @Test
    public void givenJumpInstruction() throws Exception {
        vm.getStack().push(25);
        vm.getStack().push(3);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new JumpRelativeInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new DuplicateInstruction());
        vm.execute(instructions);

        Assert.assertEquals(25, vm.getStack().pop());
        Assert.assertEquals(25, vm.getStack().pop());
    }

    @Test
    public void givenJumpOnFalseInstruction_withFalseCondition() throws Exception {
        vm.getStack().push(25);
        vm.getStack().push(3);
        vm.getStack().push(0);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new JumpOnFalseInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new DuplicateInstruction());
        vm.execute(instructions);

        Assert.assertEquals(25, vm.getStack().pop());
        Assert.assertEquals(25, vm.getStack().pop());
    }

    @Test
    public void givenJumpOnFalseInstruction_withTrueCondition() throws Exception {
        vm.getStack().push(25);
        vm.getStack().push(3);
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new JumpOnFalseInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new DuplicateInstruction());
        vm.execute(instructions);

        Assert.assertEquals(0, vm.getStack().size());
    }


    @Test
    public void givenJumpOnTrueInstruction_withTrueCondition() throws Exception {
        vm.getStack().push(25);
        vm.getStack().push(3);
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new JumpOnTrueInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new DuplicateInstruction());
        vm.execute(instructions);

        Assert.assertEquals(25, vm.getStack().pop());
        Assert.assertEquals(25, vm.getStack().pop());
    }
}
