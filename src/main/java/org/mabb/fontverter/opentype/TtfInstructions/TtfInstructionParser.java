/*
 * Copyright (C) Matthew Abboud 2016
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

import org.mabb.fontverter.io.FontDataInputStream;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class TtfInstructionParser {
    private static List<Class> instructionTypes;
    private static final Object factoryLock = new Object();
    private static Logger log = LoggerFactory.getLogger(TtfInstructionParser.class);

    InstructionStack stack = new InstructionStack();

    public List<TtfInstruction> parse(byte[] data) throws IOException, InstantiationException, IllegalAccessException {
        List<TtfInstruction> instructions = new LinkedList<TtfInstruction>();

        FontDataInputStream in = new FontDataInputStream(data);
        while (in.available() > 0) {
            int code = in.readByte() & 0xFF;

            TtfInstruction instruction = createFromCode(code);
            if (instruction == null) {
                log.info("No instruction found for code: 0x" + Integer.toHexString(code) + "/" + code);
                log.info("Position: " + in.getPosition() + " Length: " + data.length);
                break;
            }

            instruction.stack = stack;

            instruction.read(in);
            instructions.add(instruction);
            log.info("Parsed instruction: " + instruction.getClass().getSimpleName() + " code: 0x" + Integer.toHexString(code) + "/" + code);
        }

        return instructions;
    }

    public static TtfInstruction createFromCode(int code)
            throws IllegalAccessException, InstantiationException, IOException {
        initInstructionTypes();

        for (Class typeOn : instructionTypes) {
            TtfInstruction instructOn = (TtfInstruction) typeOn.newInstance();

            if (instructOn.doesMatch(code)) {
                instructOn.code = code;
                return instructOn;
            }
        }

        return null;
    }

    private static void initInstructionTypes() {
        synchronized (factoryLock) {
            if (instructionTypes == null) {
                Reflections reflections = new Reflections("org.mabb.fontverter");
                Set<Class<? extends TtfInstruction>> adapterClasses = reflections.getSubTypesOf(TtfInstruction.class);
                instructionTypes = Arrays.asList(adapterClasses.toArray(new Class[adapterClasses.size()]));
            }
        }
    }

    InstructionStack getStack() {
        return stack;
//        return (Stack<Byte>) stack.clone();
    }

    public static abstract class TtfInstruction {
        public InstructionStack stack;
        public int code;

        public abstract int[] getCodeRanges();

        public abstract void read(FontDataInputStream in) throws IOException;

        public boolean doesMatch(int code) {
            int[] range = getCodeRanges();
            if (getCodeRanges().length == 1)
                return code == range[0];
            else
                return code >= range[0] && code <= range[1];
        }
    }

    public static class PushNBytes extends TtfInstruction {
        private byte numBytes;
        private byte[] bytes;

        public int[] getCodeRanges() {
            return new int[]{0x40};
        }

        public void read(FontDataInputStream in) throws IOException {
            // todo seperate execute and read methods? only doin
            numBytes = in.readByte();
            bytes = in.readBytes(numBytes);
            for (byte byteOn : bytes)
                stack.push(byteOn);
        }
    }

    public static class PushBytes extends TtfInstruction {
        private int numBytes;
        private byte[] bytes;

        public int[] getCodeRanges() {
            return new int[]{0xB0, 0xB7};
        }

        public void read(FontDataInputStream in) throws IOException {
            numBytes = code - 0xB0 + 1;
            if (in.available() < numBytes)
                throw new IOException("Num PushBytes greater than available input stream data.");

            bytes = in.readBytes(numBytes);
            for (byte byteOn : bytes)
                stack.push(byteOn);
        }
    }

    public static class PushWords extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0xB8, 0xBF};
        }

        public void read(FontDataInputStream in) throws IOException {
            int numWords = code - 0xB8 + 1;
            in.readBytes(numWords * 2);
        }
    }

    public static class CallFunction extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0x2B};
        }

        public void read(FontDataInputStream in) throws IOException {
            long func = stack.popUint32();
            // todo
        }
    }

    public static class SetZonePointer2 extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0x15};
        }

        public void read(FontDataInputStream in) throws IOException {
        }
    }

    public static class InterpolateUntouchedPoints extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0x30, 0x31};
        }

        public void read(FontDataInputStream in) throws IOException {
        }
    }

    public static class AdjustAngle extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0x7F};
        }

        public void read(FontDataInputStream in) throws IOException {
            // From spec: 'This instruction is anachronistic and has no other effect.'
            stack.popUint32();
        }
    }

    public static class AbsoluteValue extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0x64};
        }

        public void read(FontDataInputStream in) throws IOException {
            double n = stack.popF26Dot6();
            stack.pushF26Dot6((float) Math.abs(n));
        }
    }

    public static class AddInstruction extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0x60};
        }

        public void read(FontDataInputStream in) throws IOException {
            float n1 = stack.popF26Dot6();
            float n2 = stack.popF26Dot6();

            // todo seperate execute and read methods?
            stack.pushF26Dot6(n1 + n2);
        }
    }

    public static class AlignPoints extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0x27};
        }

        public void read(FontDataInputStream in) throws IOException {
            long p1 = stack.popUint32();
            long p2 = stack.popUint32();
        }
    }

    public static class AlignToReferencePoint extends TtfInstruction {
        public int[] getCodeRanges() {
            return new int[]{0x3C};
        }

        public void read(FontDataInputStream in) throws IOException {
            long p1 = stack.popUint32();
            long p2 = stack.popUint32();
        }
    }
}

