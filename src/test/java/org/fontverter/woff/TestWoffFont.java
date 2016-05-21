package org.fontverter.woff;

import org.fontverter.io.FontDataInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestWoffFont {
    @Test
    public void givenOneByteUIntBase128_ByteDataInputStreamReadsCorrectly() throws Exception {
        byte data[] = new byte[]{0x3F};

        FontDataInputStream in = new FontDataInputStream(data);
        int readerResult = in.readUIntBase128();

        Assert.assertEquals(63, readerResult);
    }

    @Test
    public void oneByteInt_writenAsUIntBase128_thenInputStreamReadsCorrectly() throws Exception {
        int oneByteInt = 99;
        WoffOutputStream out = new WoffOutputStream();
        out.writeUIntBase128(oneByteInt);
        byte[] data = out.toByteArray();

        int readerResult = readUIntBase128(data);
        Assert.assertEquals(oneByteInt, readerResult);
    }

    @Test
    public void minTwoByteInt_writenAsUIntBase128_thenInputStreamReadsCorrectly() throws Exception {
        int oneByteInt = 128;
        int read = writeAndReadUIntBase128(oneByteInt);

        Assert.assertEquals(oneByteInt, read);
    }

    @Test
    public void maxOneByteInt_writenAsUIntBase128_thenInputStreamReadsCorrectly() throws Exception {
        int oneByteInt = 127;
        int read = writeAndReadUIntBase128(oneByteInt);

        Assert.assertEquals(oneByteInt, read);
    }

    @Test
    public void fourByteInt_writenAsUIntBase128_thenInputStreamReadsCorrectly() throws Exception {
        int oneByteInt = 2134567890;
        int readerResult = writeAndReadUIntBase128(oneByteInt);

        Assert.assertEquals(oneByteInt, readerResult);
    }

    @Test
    public void threeByteInt_writenAsUIntBase128_thenInputStreamReadsCorrectly() throws Exception {
        int oneByteInt = 16000000;
        int readerResult = writeAndReadUIntBase128(oneByteInt);

        Assert.assertEquals(oneByteInt, readerResult);
    }

    @Test
    public void givenFlagEnum_writtenAs6BitFlagValue_2bittransform_thenInputReadsCorrect() throws Exception {
        WoffOutputStream out = new WoffOutputStream();
        out.writeFlagByte(WoffConstants.TableFlagType.maxp.getValue(), 0);
        FontDataInputStream in = new FontDataInputStream(out.toByteArray());

        int[] split = in.readSplitBits(6);
        int flag = split[1];

        Assert.assertEquals(4, flag);
    }

    @Test(expected = IOException.class)
    public void oneByteUIntBase128_withInvalidSigBit_throwsException() throws Exception {
        byte data[] = new byte[]{(byte) 0x81};

        FontDataInputStream in = new FontDataInputStream(data);
        int readerResult = in.readUIntBase128();
    }

    @Test(expected = IOException.class)
    public void twoByteUIntBase128_lastByteInvalidSigBit_throwsException() throws Exception {
        byte data[] = new byte[]{(byte) 0x81, (byte) 0x83};

        FontDataInputStream in = new FontDataInputStream(data);
        int readerResult = in.readUIntBase128();
    }

    private int writeAndReadUIntBase128(int oneByteInt) throws IOException {
        WoffOutputStream out = new WoffOutputStream();
        out.writeUIntBase128(oneByteInt);
        byte[] data = out.toByteArray();

        return readUIntBase128(data);
    }

    private int readUIntBase128(byte[] data) throws IOException {
        FontDataInputStream in = new FontDataInputStream(data);
        return in.readUIntBase128();
    }
}
