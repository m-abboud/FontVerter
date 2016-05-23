package org.mabb.fontverter.woff;

import org.apache.commons.lang3.StringUtils;
import org.mabb.fontverter.io.FontDataOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WoffOutputStream extends FontDataOutputStream {
    private static Logger log = LoggerFactory.getLogger(WoffOutputStream.class);

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
        byte byteOn = (Byte.parseByte(binary, 2));
        write(byteOn);
    }

}
