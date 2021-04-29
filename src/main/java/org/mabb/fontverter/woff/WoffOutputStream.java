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

package org.mabb.fontverter.woff;

import org.apache.commons.lang3.StringUtils;
import org.mabb.fontverter.io.FontDataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WoffOutputStream extends FontDataOutputStream {
//    private static Logger log = LoggerFactory.getLogger(WoffOutputStream.class);

    public WoffOutputStream() {
        super(OPEN_TYPE_CHARSET);
    }

    public void writeUIntBase128(int num) throws IOException {
        List<Byte> bytes = new ArrayList<Byte>();
        String binary = Integer.toBinaryString(num);

        while (!binary.isEmpty()) {
            if (binary.length() < 7)
                binary = StringUtils.repeat("0", 7 - (binary.length() % 7)) + binary;

            String byteBinary = binary.substring(binary.length() - 7, binary.length());

            // last (or only) byte signficant bit must be 0 all others sig bit must be 1
            int sigbit = bytes.size() == 0 ? 0 : 128;
            byte byteOn = (byte) (sigbit + Integer.parseInt(byteBinary, 2));
            bytes.add(0, byteOn);

            binary = binary.substring(0, binary.length() - 7);
        }

        for (byte byteOn : bytes)
            write(byteOn);
    }

    public void writeFlagByte(int flag, int transform) throws IOException {
        String binary = Integer.toBinaryString(flag);
        String transBinary = Integer.toBinaryString(transform);
        if (transBinary.length() < 2)
            transBinary = StringUtils.repeat("0", 2 - transBinary.length()) + transBinary;
        if (binary.length() < 6)
            binary = StringUtils.repeat("0", 6 - binary.length()) + binary;

        binary = transBinary + binary;
        byte byteOn = (byte) (Integer.parseInt(binary, 2));

        write(byteOn);
    }

}
