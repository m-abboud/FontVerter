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

public class TestVmArithmeticInstructions {
    private TtfInstructionParser parser;
    private TtfVirtualMachine vm;

    @Before
    public void init() {
        parser = new TtfInstructionParser();
        vm = new TtfVirtualMachine(new FontDataInputStream(new byte[0]));
    }

    @Test
    public void givenGreaterOrEqualsInstructionWithSameNums_whenExecuted_pushesTrue() throws Exception {
        vm.getStack().push(5);
        vm.getStack().push(5);

        vm.execute(new GreaterOrEqualsInstruction());

        Assert.assertEquals(1L, vm.getStack().pop());
    }

    @Test
    public void givenGreaterThanInstructionWithFirstNumBigger_whenExecuted_pushesTrue() throws Exception {
        vm.getStack().push(5);
        vm.getStack().push(3);

        vm.execute(new GreaterThanInstruction());

        Assert.assertEquals(1L, vm.getStack().pop());
    }

    @Test
    public void givenEvenInstructionWithUnEvenFloat_whenExecuted_pushesFalse() throws Exception {
        vm.getStack().push(11f);

        vm.execute(new EvenInstruction());

        Assert.assertEquals(0L, vm.getStack().pop());
    }

    @Test
    public void givenFloorInstruction_whenExecuted_thenWholeNumFloorPushed() throws Exception {
        vm.getStack().push(2.7f);

        vm.execute(new FloorInstruction());

        Assert.assertEquals(2f, vm.getStack().pop());
    }

    @Test
    public void givenCeilingInstruction_whenExecuted_thenWholeNumCeilingPushed() throws Exception {
        vm.getStack().push(2.7f);

        vm.execute(new CeilingInstruction());

        Assert.assertEquals(3f, vm.getStack().pop());
    }

    @Test
    public void givenEvenInstructionWithEvenFloat_whenExecuted_pushesTrue() throws Exception {
        vm.getStack().push(12f);

        vm.execute(new EvenInstruction());

        Assert.assertEquals(1L, vm.getStack().pop());
    }

    @Test
    public void givenEqualsInstruction_withUnEqualElements_whenExecuted_pushesFalse() throws Exception {
        vm.getStack().push(11L);
        vm.getStack().push(5L);

        vm.execute(new EqualsInstruction());

        Assert.assertEquals(0L, vm.getStack().pop());
    }

    @Test
    public void givenEqualsInstruction_withEqualElements_whenExecuted_pushesTrue() throws Exception {
        vm.getStack().push(11L);
        vm.getStack().push(11L);

        vm.execute(new EqualsInstruction());

        Assert.assertEquals(1L, vm.getStack().pop());
    }

    @Test
    public void givenDivideInstruction_whenExecuted_thenDivisionResultPushed() throws Exception {
        vm.getStack().push(4f);
        vm.getStack().push(2f);

        vm.execute(new DivideInstruction());

        // Spec a little odd: The division takes place in the following fashion,
        // n1 is shifted left by six bits and then divided by 2.
        Assert.assertEquals(32f, vm.getStack().pop());
    }
}
