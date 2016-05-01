package org.fontverter.cff;

import org.fontverter.FontWriter;
import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OpenTypeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class CFFToOpenTypeConverter
{
    private static Logger log = LoggerFactory.getLogger(CFFToOpenTypeConverter.class);

    private final byte[] cffData;
    FontWriter out = FontWriter.createWriter();
    Charset charset = Charset.forName("ISO-8859-1");

    public CFFToOpenTypeConverter(byte[] cffData)
    {
        this.cffData = cffData;
    }

    public byte[] generateOpenTypeFont() throws IOException
    {
        OpenTypeFont font = OpenTypeFont.createBlankFont();
        font.addTable(new CFFTable(this.cffData));

        List<OpenTypeTable> tables = font.getTables();
        createSfntHeader(tables);

        calculateOffsets(tables);
        for (OpenTypeTable tableOn : tables)
            out.write(tableOn.getRecordEntry());

        for (OpenTypeTable tableOn : tables)
            out.write(tableOn.getData());

        byte[] bytes = out.toByteArray();
//        for (byte byteOn : bytes)
//            System.out.println(byteOn);
        return bytes;
    }

    private void calculateOffsets(List<OpenTypeTable> tables) throws IOException
    {
        // must calculate table record offsets before we write any table data
        // start data offsets after sfnt header and table records
        int offset = tables.size() * OpenTypeTable.TABLE_RECORD_SIZE + out.size();
        for (OpenTypeTable tableOn : tables)
        {
            tableOn.setOffset(offset);
            log.debug("{} Offset Calc: {}", tableOn.getName(), offset);

            offset += tableOn.getData().length;
        }
    }

    private void createSfntHeader(List<OpenTypeTable> tables) throws IOException
    {
        int numTables = tables.size();
        int searchRange = closestMaxPowerOfTwo(numTables) * 16;
        int entrySelector = (int) Math.log(closestMaxPowerOfTwo(numTables));
        int rangeShift = numTables * 16 - searchRange;

        out.write("OTTO".getBytes(charset));
        out.writeShort(numTables);
        out.writeShort(searchRange);
        out.writeShort(entrySelector);
        out.writeShort(rangeShift);
    }

    private int closestMaxPowerOfTwo(double number)
    {
        int powerOfTwo = 1;
        while (powerOfTwo * 2 < number)
        {
            powerOfTwo = powerOfTwo * 2;
        }
        return powerOfTwo;
    }

}