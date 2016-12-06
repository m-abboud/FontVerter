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
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.TtfInstructions.graphicsengine.RoundSettings;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.graphic.*;

import java.io.IOException;

import static org.mabb.fontverter.opentype.TtfInstructions.graphicsengine.RoundSettings.RoundState.ROUND_DOWN_TO_GRID;

public class TestGraphicsStateInstructions {
    public static final float DELTA = 0.0000001f;
    private TtfInstructionParser parser;
    private TtfVirtualMachine vm;
    private OpenTypeFont font;

    @Before
    public void init() throws IOException {
        parser = new TtfInstructionParser();
        font = OpenTypeFont.createBlankTtfFont();

        vm = new TtfVirtualMachine(font);
    }

    @Test
    public void givenReadControlValueEntryInstruction_whenExecuted_thenPushesCvtValue() throws Exception {
        font.getCvt().getValues().add((short) 123);
        vm.getStack().push(0l);

        vm.getGraphicsState().initialize(font);
        vm.execute(new ReadCvtEntryInstruction());

        Assert.assertEquals(123f, vm.getStack().pop());
    }


    @Test
    public void givenWriteCvtEntryInstruction_whenExecuted_thenWriteCvtValue() throws Exception {
        font.getCvt().getValues().add((short) 123);
        vm.getStack().push(0L);
        vm.getStack().push(33L);

        vm.getGraphicsState().initialize(font);
        vm.execute(new WriteCvtTableFunitsInstruction());

        Assert.assertEquals(33L, vm.getGraphicsState().getCvtValue(0L).longValue());
    }

    @Test
    public void givenRoundDownToGridInstruction_whenExecuted_thenRoundStateSet() throws Exception {
        vm.execute(new RoundDownToGridInstruction());

        Assert.assertEquals(ROUND_DOWN_TO_GRID, vm.getGraphicsState().roundState);
    }

    @Test
    public void givenSuperRound45DegInstruction_thenGraphicsStateSuperRound_phase_period_and_threshold_set()
            throws Exception {
        // top 4 bits = threshold, next 2 bits = phase, next 2 = period
        // 170L = 10 10 1010
        vm.getStack().push(170L);

        vm.execute(new SuperRound45DegInstruction());

        RoundSettings settings = vm.getGraphicsState().roundSettings;

        Assert.assertEquals(2.0 * Math.sqrt(2), settings.period, DELTA);
        Assert.assertEquals(settings.period / 2.0, settings.phase, DELTA);
        Assert.assertEquals(6.0 / 8.0 * settings.period, settings.threshold, DELTA);
    }

    @Test
    public void givenSuperRoundInstruction_thenGraphicsStateSuperRound_phase_period_and_threshold_set()
            throws Exception {
        // top 4 bits = threshold, next 2 bits = phase, next 2 = period
        // 170L = 10 10 1010
        vm.getStack().push(170L);

        vm.execute(new SuperRoundInstruction());

        RoundSettings settings = vm.getGraphicsState().roundSettings;

        Assert.assertEquals(2.0, settings.period, DELTA);
        Assert.assertEquals(settings.period / 2.0, settings.phase, DELTA);
        Assert.assertEquals(6.0 / 8.0 * settings.period, settings.threshold, DELTA);
    }

    @Test
    public void givenSetAngleWeightInstruction_whenExecuted_thenGraphicsStateWeightSet() throws Exception {
        vm.getStack().push(555L);

        vm.execute(new SetAngleWeightInstruction());

        Assert.assertEquals(555L, vm.getGraphicsState().angleWeight.longValue());
    }

    @Test
    public void givenScanConversionControlInstruction_whenExecuted_thenScanControlDropoutModesSet()
            throws Exception {
        // top 8 bits = threshold, next 4 is dropout mode flags
        // 0x2AAA = 101010 10101010
        vm.getStack().push(0x2AAA);

        vm.execute(new ScanConversionControlInstruction());

        Assert.assertEquals(0xAA, vm.getGraphicsState().droputThreshold);
        Assert.assertEquals(3, vm.getGraphicsState().dropoutControlModes.size());
    }

    @Test
    public void givenScanTypeInstruction_whenExecuted_thenScanConverterModeSet() throws Exception {
        vm.getStack().push(1);

        vm.execute(new ScanTypeInstruction());

        Assert.assertEquals(TtfGraphicsState.ScanConverterMode.DROPOUT_EXCLUDING_STUBS, vm.getGraphicsState().scanConverterMode);
    }

    @Test
    public void givenDeltaBaseInstruction_whenExecuted_thenDeltaBaseSet() throws Exception {
        vm.getStack().push(55L);
        vm.execute(new SetDeltaBaseInstruction());

        Assert.assertEquals(55L, vm.getGraphicsState().deltaBase.longValue());
    }

    @Test
    public void givenDeltaShiftInstruction_whenExecuted_thenDeltaShiftSet() throws Exception {
        vm.getStack().push(22L);
        vm.execute(new SetDeltaShiftInstruction());

        Assert.assertEquals(22L, vm.getGraphicsState().deltaShift.longValue());
    }

    @Test
    public void givenSetFreedomVectorInstruction_whenExecuted_thenFreedomVectorModified() throws Exception {
        vm.getStack().push(.5f);
        vm.getStack().push(.8f);
        vm.execute(new SetFreedomVectorInstruction());

        Assert.assertEquals(0.5f, vm.getGraphicsState().freedomVector.x, DELTA);
        Assert.assertEquals(0.8f, vm.getGraphicsState().freedomVector.y, DELTA);
    }

    @Test
    public void givenSetFreedomVectorToAxisInstruction_whenExecuted_thenSetToXAxis() throws Exception {
        SetFreedomVectorToAxisInstruction instruction = new SetFreedomVectorToAxisInstruction();
        instruction.isXAxis = true;

        vm.execute(instruction);

        Assert.assertEquals(0.0f, vm.getGraphicsState().freedomVector.x, DELTA);
    }

    @Test
    public void givenSetFreedomVectorToProjectionInstruction_whenExecuted_thenSetToXAxis() throws Exception {
        vm.getGraphicsState().projectionVector.y = 0.42;
        vm.execute(new SetFreedomVectorToProjectionVectorInstruction());

        Assert.assertEquals(0.42f, vm.getGraphicsState().freedomVector.y, DELTA);
    }

    @Test
    public void givenSetZonePointerStoGlyphZone_whenExecuted_thenAllThreeZonePointersSetToGlyphZone()
            throws Exception {
        vm.getStack().push(1L);
        vm.execute(new SetZonePointerSInstruction());

        Assert.assertEquals(1, (long) vm.getGraphicsState().zone0Id);
        Assert.assertEquals(1, (long) vm.getGraphicsState().zone1Id);
        Assert.assertEquals(1, (long) vm.getGraphicsState().zone2Id);
    }
}
