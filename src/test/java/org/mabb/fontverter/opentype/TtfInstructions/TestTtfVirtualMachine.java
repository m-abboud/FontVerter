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

import java.util.List;

public class TestTtfVirtualMachine {
    private TtfInstructionParser parser;
    private TtfVirtualMachine vm;

    @Before
    public void init() {
        parser = new TtfInstructionParser();
        vm = new TtfVirtualMachine(new FontDataInputStream(new byte[0]));
    }

    @Test
    public void givenDuplicateInstruction_whenExecuted_thenElementPushedTwice() throws Exception {
        vm.getStack().push(1L);

        vm.execute(new DuplicateInstruction());

        Assert.assertEquals(1L, vm.getStack().pop());
        Assert.assertEquals(1L, vm.getStack().pop());
    }

    @Test
    public void givenDepthInstruction_whenExecuted_thenStackSizePushed() throws Exception {
        vm.getStack().push(55);
        vm.getStack().push(0);

        vm.execute(new DepthInstruction());

        Assert.assertEquals(2, vm.getStack().pop());
    }

    @Test
    public void givenClearInstruction_whenExecuted_thenStackIsCleared() throws Exception {
        vm.getStack().push(55);
        vm.getStack().push(0);
        vm.getStack().push(33);

        vm.execute(new ClearInstruction());

        Assert.assertEquals(0, vm.getStack().size());
    }

    @Test
    public void givenCIndexInstruction_whenExecuted_thenElementAtIndexPushed() throws Exception {
        // kth element
        vm.getStack().push(55);
        // index
        vm.getStack().push(0);

        vm.execute(new CIndexInstruction());

        // check copy of first element has been pushed
        Assert.assertEquals(55, vm.getStack().pop());
        Assert.assertEquals(55, vm.getStack().pop());
    }

    @Test
    public void givenAndInstruction_with2NonZerosOnStack_whenExecuted_thenTrueIsPushed() throws Exception {
        vm.getStack().push(2L);
        vm.getStack().push(3L);

        vm.execute(new AndInstruction());

        Assert.assertEquals(1L, vm.getStack().pop());
    }

    @Test
    public void givenAndInstruction_with1Zero1NonZeroOnStack_whenExecuted_thenFalseIsPushed() throws Exception {
        vm.getStack().push(0L);
        vm.getStack().push(3L);

        vm.execute(new AndInstruction());

        Assert.assertEquals(0L, vm.getStack().pop());
    }

    @Test
    public void givenPushNBytesInstrctionWith1Byte_whenExecuted_then1BytePushed() throws Exception {
        // 0x40 = code next byte is num bytes and last is the actual byte to push
        byte[] instructions = new byte[]{0x40, 0x01, 0x01};

        List<TtfInstruction> parsed = parser.parse(instructions);

        TtfVirtualMachine vm = new TtfVirtualMachine(new FontDataInputStream(instructions));
        vm.execute(parsed);
        Assert.assertEquals(1, vm.getStack().size());
    }

    @Test
    public void givenPushBytesInstrctionWith2Bytes_whenExecuted_then2BytesPushed() throws Exception {
        byte[] instructions = new byte[]{(byte) 0xB1, 0x01, 0x05};

        List<TtfInstruction> parsed = parser.parse(instructions);
        TtfVirtualMachine vm = new TtfVirtualMachine(new FontDataInputStream(instructions));
        vm.execute(parsed);

        Assert.assertEquals(2, vm.getStack().size());
    }

    @Test
    public void givenAbsInstrctionWithNegativeOnStack_whenExecuted_then2PosValPushed() throws Exception {
        byte[] instructions = new byte[]{(byte) 0x64};

        List<TtfInstruction> parsed = parser.parse(instructions);
        TtfVirtualMachine vm = new TtfVirtualMachine(new FontDataInputStream(instructions));
        vm.getStack().push(-31.4f);
        vm.execute(parsed);

        Assert.assertEquals(31.4f, vm.getStack().popF26Dot6(), 2);
    }
}
