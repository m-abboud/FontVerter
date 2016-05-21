package org.fontverter.opentype;

import org.fontverter.FontVerterUtils;
import org.fontverter.io.FontDataOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestOpenTypeTable {
    @Test
    public void dataLengthInDivisibleByFourRemainder1_whenGeneratingData_thenBytePaddingIsAdded()
            throws Exception {
        CannedOpenTypeTable table = new CannedOpenTypeTable();
        table.fillerData = new byte[]{0};

        byte[] outputData = table.getData();

        Assert.assertEquals(4, outputData.length);
    }

    @Test
    public void dataLengthInDivisibleByFourRemainder3_whenGeneratingData_thenBytePaddingIsAdded()
            throws Exception {
        CannedOpenTypeTable table = new CannedOpenTypeTable();
        table.fillerData = new byte[]{0, 0, 0};

        byte[] outputData = table.getData();

        Assert.assertEquals(4, outputData.length);
    }

    @Test
    public void dataLengthDivisibleByFour_whenGeneratingData_then_NO_BytePaddingAdded() throws Exception {
        CannedOpenTypeTable table = new CannedOpenTypeTable();
        table.fillerData = new byte[]{0, 0, 0, 0};

        byte[] outputData = table.getData();

        Assert.assertEquals(4, outputData.length);
    }

    @Test
    public void fourBytesOfData_thenTableChecksumIsSumOfLongs() throws Exception {
        // note longs in opentype are 32bit vs java 64
        CannedOpenTypeTable table = new CannedOpenTypeTable();
        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        writer.writeInt(1234567);
        table.fillerData = writer.toByteArray();

        long checksum = table.checksum();

        Assert.assertEquals(1234567, checksum);
    }

    @Test
    public void _16BytesOfData_thenTableChecksumIsSumOfLongs() throws Exception {
        // note longs in opentype are 32bit vs java 64
        CannedOpenTypeTable table = new CannedOpenTypeTable();

        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        int[] dataLongs = new int[]{500, 1000, 1234567, 991076541};
        int expectedChecksum = 0;
        for (int intOn : dataLongs) {
            expectedChecksum += intOn;
            writer.writeUnsignedInt(intOn);
        }
        table.fillerData = writer.toByteArray();

        long checksum = table.checksum();

        Assert.assertEquals(expectedChecksum, checksum);
    }

    @Test
    public void _2BytesOfData_thenTableChecksumDoesNotThrowException() throws Exception {
        // if internal methods don't pad checksum calc won't be able to read 4 bytes at time
        CannedOpenTypeTable table = new CannedOpenTypeTable();
        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        writer.writeUnsignedShort(55);
        table.fillerData = writer.toByteArray();

        long checksum = table.checksum();

        Assert.assertEquals(55 << 16, checksum);
    }

    private class CannedOpenTypeTable extends OpenTypeTable {
        byte[] fillerData;

        protected byte[] generateUnpaddedData() throws IOException {
            return fillerData;
        }

        public long checksum() throws IOException {
            return FontVerterUtils.getTableChecksum(getData());
        }

        public String getName() {
            return "test";
        }
    }
}
