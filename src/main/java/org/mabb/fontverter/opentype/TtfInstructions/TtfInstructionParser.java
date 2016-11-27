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

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;
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

}

