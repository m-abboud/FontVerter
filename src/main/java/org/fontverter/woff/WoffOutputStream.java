package org.fontverter.woff;

import org.apache.commons.lang3.StringUtils;
import org.fontverter.io.ByteDataOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class WoffOutputStream extends ByteDataOutputStream{
    public WoffOutputStream() {
        super(openTypeCharset);
    }

    // broken still
    public void writeUIntBase128(int num) throws IOException {
        String base128code = Integer.toString(num, 128);
        String binary = Integer.toBinaryString(119241647);
        while(!binary.isEmpty()){
            if(binary.length() < 7)
                binary += StringUtils.repeat("0", binary.length() % 7);

            String byteBinary = "1" + binary.substring(0, 6);
            byte byteOn = (byte)Integer.parseInt(byteBinary, 2);

//            byte byteOn = Byte.parseByte(byteBinary, 2);
            write(byteOn);
            binary = binary.substring(6, binary.length());
        }


        // Pseduo reader code from woff spec:

//        Integer.highestOneBit()num
//        UInt32 accum = 0;
//
//        for (i = 0; i < 5; i++) {
//            UInt8 data_byte = data.getNextUInt8();
//
//            // No leading 0's
//            if (i == 0 && data_byte = 0x80) return false;
//
//            // If any of top 7 bits are set then << 7 would overflow
//            if (accum & 0xFE000000) return false;
//
//            *accum = (accum << 7) | (data_byte & 0x7F);
//
//            // Spin until most significant bit of data byte is false
//            if ((data_byte & 0x80) == 0) {
//                *result = accum;
//                return true;
//            }
//        }
//        // UIntBase128 sequence exceeds 5 bytes
//        return false;
    }

}
