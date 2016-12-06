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
import org.mabb.fontverter.io.FontDataOutputStream;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.*;

import java.util.ArrayList;
import java.util.List;

public class TestControlFlowInstructions {
    private TtfInstructionParser parser;
    private TtfVirtualMachine vm;

    @Before
    public void init() {
        parser = new TtfInstructionParser();
        vm = new TtfVirtualMachine(null);
    }

    @Test
    public void givenJumpInstruction_whenExecuted_thenVmSkips3Instructions() throws Exception {
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
    public void givenJumpOnFalseInstruction_withFalseCondition_whenExecuted_thenSkipsInstructions() throws Exception {
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
    public void givenJumpOnFalseInstruction_withTrueCondition_whenExecuted_thenDoesNotSkip() throws Exception {
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
    public void givenJumpOnTrueInstruction_withTrueCondition_whenExecuted_thenSkipsInstructions() throws Exception {
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

    @Test
    public void givenSetLoopVarInstruction_whenExecuted_thenVmLoopVarSet() throws Exception {
        vm.getStack().push(33);

        vm.execute(new SetLoopVariableInstruction());

        Assert.assertEquals(33, vm.getLoopVar().longValue());
    }

    @Test
    public void givenPushWords_withNumWordsCodeParmTwo_whenExecuted_thenTwoWordsPushedAsInts() throws Exception {
        PushWords instruction = new PushWords();
        // 0xB9 num words = 2
        instruction.code = 0xB9;

        // since PushWords grabs things from the font input stream we must fake that too
        FontDataOutputStream params = new FontDataOutputStream();
        params.writeShort(55);
        params.writeShort(42);
        instruction.read(new FontDataInputStream(params.toByteArray()));


        vm.execute(instruction);

        Assert.assertEquals(42, (int) vm.getStack().popInt32());
        Assert.assertEquals(55, (int) vm.getStack().popInt32());
    }

    @Test
    public void givenPushNWords_withTwoOnStream_whenExecuted_thenTwoWordsPushedAsInts() throws Exception {
        PushNWords instruction = new PushNWords();

        // since PushNWords grabs things from the font input stream we must fake that too
        FontDataOutputStream params = new FontDataOutputStream();
        params.writeByte(2);
        params.writeShort(55);
        params.writeShort(42);
        instruction.read(new FontDataInputStream(params.toByteArray()));


        vm.execute(instruction);

        Assert.assertEquals(42, (int) vm.getStack().popInt32());
        Assert.assertEquals(55, (int) vm.getStack().popInt32());
    }

    @Test
    public void givenPushBytes_withNumBytesCodeParmTwo_whenExecuted_thenTwoBytesPushedAsInts() throws Exception {
        PushBytes instruction = new PushBytes();
        // 0xB9 num words = 2
        instruction.code = 0xB1;

        // since PushBytes grabs things from the font input stream we must fake that too
        FontDataOutputStream params = new FontDataOutputStream();
        params.writeByte(55);
        params.writeByte(42);
        instruction.read(new FontDataInputStream(params.toByteArray()));


        vm.execute(instruction);

        Assert.assertEquals(42, (int) vm.getStack().popInt32());
        Assert.assertEquals(55, (int) vm.getStack().popInt32());
    }
}
