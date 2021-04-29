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
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
    TTF uses it's own VM and instruction set to execute font hinting instructions. Crazy right.
    Parsing the instructions is of intrest for us for fixing broken old fonts that don't render
    in chrome correctley and is not really currentley of use for the actual font conversions we do.
    Instruction set can be found here:
    https://developer.apple.com/fonts/TrueType-Reference-Manual/RM05/Chap5.html
 */
public class TtfInstructionParser {
	
	private static ConcurrentHashMap<Integer, Class<? extends TtfInstruction>> instructionTypes = new ConcurrentHashMap<>();

    private static Logger log = LoggerFactory.getLogger(TtfInstructionParser.class);

    public List<TtfInstruction> parse(byte[] data) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        List<TtfInstruction> instructions = new LinkedList<TtfInstruction>();

        FontDataInputStream in = new FontDataInputStream(data);
        while (in.available() > 0) {
            int code = in.readByte() & 0xFF;

            TtfInstruction instruction = createFromCode(code);
            if (instruction == null) {
                log.error("No instruction found for code: 0x" + Integer.toHexString(code) + "/" + code);
                log.error("Position: " + in.getPosition() + " Length: " + data.length);
                break;
            }

            instruction.read(in);
            instructions.add(instruction);
            log.info("Parsed instruction: " + instruction.getClass().getSimpleName() + " code: 0x" + Integer.toHexString(code) + "/" + code);
        }

        return instructions;
    }

    public static TtfInstruction createFromCode(int code)
            throws IllegalAccessException, InstantiationException, IOException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        initInstructionTypes();

        Class<? extends TtfInstruction> type = instructionTypes.get(code);
        if (type == null)
            return null;

        TtfInstruction instruction = type.getDeclaredConstructor().newInstance();
        instruction.code = code;

        return instruction;
    }

    private static void initInstructionTypes() throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        // uses reflection to grab all TtfInstruction implementations than grabs their code ranges and adds an entry
        // into a code=> instruction class map to be used when creating instruction objects from a code when parsing
        // to remove the need for a giant if/switch block
        if (instructionTypes.isEmpty()) {
            Reflections reflections = new Reflections("org.mabb.fontverter");
            Set<Class<? extends TtfInstruction>> instructionClasses = reflections.getSubTypesOf(TtfInstruction.class);

            for (Class<? extends TtfInstruction> typeOn : instructionClasses) {
                // instiante a test object once to grab it's code ranges to use in code=>instruction class type map
                TtfInstruction instructOn = (TtfInstruction) typeOn.getDeclaredConstructor().newInstance();

                int[] range = instructOn.getCodeRanges();
                if (range.length == 1)
                    instructionTypes.put(range[0], typeOn);
                else {
                    for (int i = range[0]; i <= range[1]; i++)
                        instructionTypes.put(i, typeOn);
                }
            }
        }
    }
}
