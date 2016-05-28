package org.mabb.fontverter.opentype;

import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.FontDataOutputStream;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.mabb.fontverter.io.FontDataOutputStream.OPEN_TYPE_CHARSET;

public class ControlValueTable extends OpenTypeTable {
    private List<Short> values = new LinkedList<Short>();
    public String getTableTypeName() {
        return "cvt ";
    }

    public void readData(byte[] data) throws IOException {
        FontDataInputStream input = new FontDataInputStream(data);
        while (input.available() > 2)
            values.add(input.readShort());
    }

    protected byte[] generateUnpaddedData() throws IOException {
        FontDataOutputStream out = new FontDataOutputStream(OPEN_TYPE_CHARSET);
        for(Short valueOn : values)
            out.writeShort(valueOn);

        return out.toByteArray();
    }

    public List<Short> getValues() {
        return values;
    }
}
