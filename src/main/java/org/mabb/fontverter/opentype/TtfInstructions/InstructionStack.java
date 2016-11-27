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

import java.io.IOException;
import java.util.Stack;

public class InstructionStack extends Stack<Object> {
    public Long popUint32() throws IOException {
        Object obj = pop();
        if (!(obj instanceof Long)) {
            String msg = "Expected type Uint32 but was type: " + obj.getClass().getSimpleName();
            throw new InstructionStackWrongTypeException(msg);
        }

        return (Long) obj;
    }

    public Float popF26Dot6() throws IOException {
       Object obj = pop();
       if (!(obj instanceof Float)) {
           String msg = "Expected type F26Dot6 but was type: " + obj.getClass().getSimpleName();
           throw new InstructionStackWrongTypeException(msg);
       }

       return (Float) obj;
    }

    public Number popNumber() throws IOException {
        Object obj = pop();
        if (!(obj instanceof Number)) {
            String msg = "Expected type number but was type: " + obj.getClass().getSimpleName();
            throw new InstructionStackWrongTypeException(msg);
        }

        return (Number) obj;
    }

    public class InstructionStackWrongTypeException extends IOException {
        public InstructionStackWrongTypeException(String message) {
            super(message);
        }
    }
}
