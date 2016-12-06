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
import org.mabb.fontverter.opentype.TtfInstructions.instructions.*;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.CallFunction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.ClearInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.EndFunctionInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.FunctionDefInstruction;

import java.util.ArrayList;
import java.util.List;

public class TestFunctionInstructions {
    private TtfInstructionParser parser;
    private TtfVirtualMachine vm;

    @Before
    public void init() {
        parser = new TtfInstructionParser();
        vm = new TtfVirtualMachine(null);
    }

    @Test
    public void givenFunctionInstruction_thenVmAddsToFunctionsMap_andFunctionHasInstructions() throws Exception {
        vm.getStack().push(1);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new FunctionDefInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndFunctionInstruction());
        // functions must be built at run time since thier ID is grabbed from the stack
        vm.execute(instructions);

        TtfFunction function = vm.getFunction(1);

        Assert.assertEquals(2, function.getInstructions().size());
    }

    @Test
    public void givenMultipleFunctions_thenVmAddsAllToFunctionsMap() throws Exception {
        vm.getStack().push(1);
        vm.getStack().push(2);
        vm.getStack().push(3);

        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new FunctionDefInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndFunctionInstruction());
        instructions.add(new FunctionDefInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndFunctionInstruction());
        instructions.add(new FunctionDefInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndFunctionInstruction());
        instructions.add(new ClearInstruction());
        // functions must be built at run time since thier ID is grabbed from the stack
        vm.execute(instructions);

        Assert.assertNotNull(vm.getFunction(1));
        Assert.assertNotNull(vm.getFunction(3));
        Assert.assertNotNull(vm.getFunction(2));
    }

    @Test
    public void givenCallFunctionInstruction_thenFunctionIsCalled() throws Exception {
        vm.getStack().push(5);
        vm.getStack().push(5);
        vm.getStack().push(1);
        vm.getStack().push(1);


        List<TtfInstruction> instructions = new ArrayList<TtfInstruction>();
        instructions.add(new FunctionDefInstruction());
        instructions.add(new ClearInstruction());
        instructions.add(new EndFunctionInstruction());
        instructions.add(new CallFunction());

        // functions must be built at run time since thier ID is grabbed from the stack
        vm.execute(instructions);

        Assert.assertEquals(0, vm.getStack().size());
    }
}
