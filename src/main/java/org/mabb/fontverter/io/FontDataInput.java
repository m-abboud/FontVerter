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

package org.mabb.fontverter.io;

import java.io.DataInput;
import java.io.IOException;

public interface FontDataInput extends DataInput {
    long readUnsignedInt() throws IOException;

    String readString(int length) throws IOException;

    byte[] readBytes(int length) throws IOException;

    void seek(int offset);

    // converted from pseduo C like reader code from woff spec
    int readUIntBase128() throws IOException;

    float readFixed32() throws IOException;

    int getPosition();

    int[] readSplitBits(int numUpperBits) throws IOException;

    int[] readUnsignedShortArray(int length) throws IOException;
}
