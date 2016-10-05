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

import org.mabb.fontverter.FontVerterUtils;
import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.FontDataOutputStream;

import java.io.IOException;
import java.util.Stack;

/**
 * todo refactor to instruction elements can't go off byte vals alone since AND function takes generic
 * input ugh
 */
public class InstructionStack extends Stack<Byte> {
    public long popUint32() throws IOException {
        FontDataInputStream input = createDataReader();
        pop(4);

        return input.readUnsignedInt();
    }

    public float popF26Dot6() throws IOException {
        FontDataInputStream input = createDataReader();
        pop(4);

        return input.readFixed32();
    }

    public void pushF26Dot6(float num) throws IOException {
        FontDataOutputStream writer = new FontDataOutputStream();
        writer.write32Fixed(num);

        push(writer.toByteArray());
    }

    private void push(byte[] bytes) {
        for(byte byteOn : bytes)
            push(byteOn);
    }

    private void pop(int numBytes) {
        for (int i = 0; i < numBytes; i++)
            this.pop();
    }

    private FontDataInputStream createDataReader() {
        Byte[] list = this.toArray(new Byte[this.size()]);
        return new FontDataInputStream(FontVerterUtils.toPrimative(list));
    }

}
