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

import org.slf4j.Logger;

import java.io.IOException;
import java.util.Stack;

import static org.slf4j.LoggerFactory.getLogger;

public class InstructionStack extends Stack<Object> {
    private static final Logger log = getLogger(InstructionStack.class);
    boolean typeCheckExceptions = false;

    public Long popUint32() throws IOException {
        Number obj = (Number) pop();
        if (!(obj instanceof Long)) {
            String msg = "Expected type Uint32 but was type: " + obj.getClass().getSimpleName();
            error(msg);
        }

        return obj.longValue();
    }

    public Integer popInt32() throws IOException {
        Number obj = (Number) pop();
        if (!(obj instanceof Integer)) {
            String msg = "Expected type int32 but was type: " + obj.getClass().getSimpleName();
            error(msg);
        }

        return obj.intValue();
    }

    public Float popF26Dot6() throws IOException {
        Number obj = (Number) pop();
        if (!(obj instanceof Float)) {
            String msg = "Expected type F26Dot6 but was type: " + obj.getClass().getSimpleName();
            error(msg);
        }

        return obj.floatValue();
    }

    public Byte popEint8() throws IOException {
        Number obj = (Number) pop();
        if (!(obj instanceof Byte)) {
            String msg = "Expected type Eint8 but was type: " + obj.getClass().getSimpleName();
            error(msg);
        }

        return obj.byteValue();
    }

    public Number popNumber() throws IOException {
        Number obj = (Number) pop();
        if (!(obj instanceof Number)) {
            String msg = "Expected type number but was type: " + obj.getClass().getSimpleName();
            error(msg);
        }

        return obj;
    }


    private void error(String msg) throws IOException {
        if (typeCheckExceptions)
            throw new InstructionStackWrongTypeException(msg);
        else
            log.debug("TTF stack pop type not expected: " + msg);
    }

    public class InstructionStackWrongTypeException extends IOException {
        public InstructionStackWrongTypeException(String message) {
            super(message);
        }
    }
}
