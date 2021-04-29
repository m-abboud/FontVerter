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

import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.control.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This is our interpreter for TTF outline hint programs. Current purpose is for figuring
 * out and fixing older TTF fonts that don't render in some browsers correctly rather than
 * actual font rasterization
 * <p>
 * Currently can handle all stack and control operations but only like 15% of the graphics state
 * related instructions
 */
public class TtfVirtualMachine implements TtfInstructionVisitor {
    private static final Logger log = getLogger(TtfVirtualMachine.class);

    public int jumpOffset = 0;

    private OpenTypeFont font;

    private InstructionStack stack;
    private Stack<IfInstruction> ifStack = new Stack<IfInstruction>();
    private int discardIf = 0;

    private TtfFunction functionOn;
    private Map<Integer, TtfFunction> functions = new HashMap<Integer, TtfFunction>();

    private Integer loopVar = 1;
    private TtfGraphicsState graphicsState;
    private Long[] storageArea;
    private boolean hasFpgmRun = false;
//    private boolean onFpgm;

    public TtfVirtualMachine(OpenTypeFont font) {
        this.font = font;

        initialize();
    }

    public void execute(List<TtfInstruction> instructions) throws IOException {
        executeFpgmInstructions();

        for (int i = 0; i < instructions.size(); i++) {
            TtfInstruction instructionOn = instructions.get(i);
            try {
                execute(instructionOn);
            } catch (Exception ex) {
                throw new TtfVmRuntimeException(String.format("Error on instruction #%d type: %s Message: %s",
                        i, instructionOn.toString(), ex.getMessage()), ex);
            }

            if (jumpOffset > 0) {
                i += jumpOffset - 1;
                jumpOffset = 0;
            }
        }
    }

    private void initialize() {
        this.stack = new InstructionStack();
        this.graphicsState = new TtfGraphicsState();
        graphicsState.initialize(font);

        // font == null for some lazy test code
        if (font != null && font.getMxap() != null)
            storageArea = new Long[font.getMxap().getMaxStorage()];
    }

    private void executeFpgmInstructions() throws IOException {
        // FPGM(Font Program) table instructions must be executed only once before anything else
        // in the font get used
        if (hasFpgmRun)
            return;

//        onFpgm = true;
        hasFpgmRun = true;
        // font == null for some lazy test code
        if (font != null && font.getFpgmTable() != null)
            execute(font.getFpgmTable().getInstructions());

//        onFpgm = false;
    }

    public void execute(TtfInstruction instruction) throws IOException {
        instruction.vm = this;

        if (functionOn == null || instruction instanceof EndFunctionInstruction)
            instruction.accept(this);
        else
            // since functions get their unique ID from the stack we must build the functions
            // at run time rather then in the parse stage
            functionOn.addInstruction(instruction);
    }

    public void visitGeneric(TtfInstruction instruction) throws IOException {
        if (!shouldExecuteBranch())
            return;

        instruction.execute(stack);
    }

    public void visit(IfInstruction instruction) throws IOException {
        if (!shouldExecuteBranch()) {
            discardIf++;
            return;
        }

        instruction.execute(stack);
        ifStack.push(instruction);
    }

    public void visit(ElseInstruction instruction) throws IOException {
        if (ifStack.size() == 0)
            throw new TtfVmRuntimeException("Else with no matching If!!");

        ifStack.peek().shouldExecute = !ifStack.peek().shouldExecute;
    }

    public void visit(EndIfInstruction instruction) throws IOException {
        if (discardIf > 0) {
            discardIf--;
            return;
        }

        if (ifStack.size() == 0)
            throw new TtfVmRuntimeException("End If with no matching If!!");

        ifStack.pop();
    }

    public void visit(FunctionDefInstruction instruction) throws IOException {
        instruction.execute(stack);

        functionOn = new TtfFunction();
        functions.put(instruction.getFunctionId(), functionOn);
    }

    public void visit(EndFunctionInstruction instruction) throws IOException {
        if (functionOn == null)
            throw new TtfVmRuntimeException("End function with no matching func def start!!");

        functionOn = null;
    }

    public boolean shouldExecuteBranch() {
        if (ifStack.size() == 0)
            return true;

        return ifStack.get(ifStack.size() - 1).shouldExecute;
    }

    public TtfFunction getFunction(Integer function) {
        return functions.get(function);
    }

    InstructionStack getStack() {
        return stack;
    }

    public OpenTypeFont getFont() {
        return font;
    }

    public TtfGraphicsState getGraphicsState() {
        return graphicsState;
    }

    public Long getStorageAreaValue(Long index) {
        if (index > storageArea.length) {
            log.error(String.format("Referenced Storage Value at Index: %d does not exist.", index));
            return 1L;
        }

        return storageArea[index.intValue()];
    }

    public void setStorageAreaValue(Long index, Long value) {
        if (index > storageArea.length) {
            log.error(String.format("Referenced Storage Value at Index: %d does not exist.", index));
            return;
        }

        storageArea[index.intValue()] = value;
    }

    public void setLoopVar(Integer loopVar) {
        this.loopVar = loopVar;
    }

    public Integer getLoopVar() {
        return loopVar;
    }

    @SuppressWarnings("serial")
	public static class TtfVmRuntimeException extends IOException {
        public TtfVmRuntimeException(String message) {
            super(message);
        }

        public TtfVmRuntimeException(String message, Exception ex) {
            super(message, ex);
        }
    }
}
