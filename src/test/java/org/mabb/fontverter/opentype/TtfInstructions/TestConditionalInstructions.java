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

import java.util.ArrayList;
import java.util.List;

public class TestConditionalInstructions {
    private TtfInstructionParser parser;
    private TtfVirtualMachine vm;

    @Before
    public void init() {
        parser = new TtfInstructionParser();
        vm = new TtfVirtualMachine(new FontDataInputStream(new byte[0]));
    }

    @Test
    public void givenTrueIfInstruction_whenExecuted_thenConditonalExecuted() throws Exception {
        vm.getStack().push(5);
        // push true
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(0, vm.getStack().size());
    }

    @Test
    public void givenFalseIfInstruction_whenExecuted_thenConditonalSkipped() throws Exception {
        vm.getStack().push(5);
        // push false
        vm.getStack().push(0);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(1, vm.getStack().size());
    }

    @Test
    public void givenFalseIfInstruction_whenExecuted_thenInstructionAfterStillExecuted() throws Exception {
        vm.getStack().push(5);
        // push false
        vm.getStack().push(0);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndIfInstruction());
        instructions.add(new DuplicateInstruction());
        vm.execute(instructions);

        Assert.assertEquals(2, vm.getStack().size());
    }


    @Test
    public void givenFalseIfInstructionWithElse_whenExecuted_thenElseExecuted() throws Exception {
        vm.getStack().push(5);
        // push true
        vm.getStack().push(0);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new ElseInstruction());
        instructions.add(new DuplicateInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(2, vm.getStack().size());
    }

    @Test
    public void givenTrueIfInstructionWithElse_whenExecuted_thenElseNotExecuted() throws Exception {
        vm.getStack().push(5);
        // push true
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new DuplicateInstruction());
        instructions.add(new ElseInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(2, vm.getStack().size());
    }

    @Test
    public void givenNestedIfStatements_innerOneFalse() throws Exception {
        vm.getStack().push(5);
        // push if(true) if(false)
        vm.getStack().push(1);
        vm.getStack().push(0);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new IfInstruction());
        instructions.add(new DuplicateInstruction());
        instructions.add(new EndIfInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(2, vm.getStack().size());
    }

    @Test
    public void givenNestedIfStatements_outerOneFalseInnerTrue() throws Exception {
        vm.getStack().push(5);
        // push if(true) {if(false)}
        vm.getStack().push(0);
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new IfInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndIfInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(1, vm.getStack().size());
    }

    @Test
    public void givenNestedIfStatements_innerFalseWithElse() throws Exception {
        vm.getStack().push(5);
        // push if(true) {if(false)}
        vm.getStack().push(0);
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new IfInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new ElseInstruction());
        instructions.add(new DuplicateInstruction());
        instructions.add(new EndIfInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(2, vm.getStack().size());
    }

    @Test
    public void givenNestedIfStatements_innerTrueWithElse() throws Exception {
        vm.getStack().push(5);
        // push if(true) {if(false)}
        vm.getStack().push(1);
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new IfInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new ElseInstruction());
        instructions.add(new DuplicateInstruction());
        instructions.add(new EndIfInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(0, vm.getStack().size());
    }

    @Test
    public void givenNestedIfStatements_innerFalseOuterHasInstructionAfter() throws Exception {
        vm.getStack().push(5);
        // push if(true) {if(false)}
        vm.getStack().push(0);
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new IfInstruction());
        instructions.add(new IfInstruction());
        instructions.add(new DuplicateInstruction());
        instructions.add(new EndIfInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndIfInstruction());
        vm.execute(instructions);

        Assert.assertEquals(0, vm.getStack().size());
    }
}
