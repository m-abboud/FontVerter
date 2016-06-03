package org.mabb.fontverter.woff;

import org.mabb.fontverter.converter.WoffToOtfConverter;
import org.mabb.fontverter.io.FontDataOutputStream;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontConverter;
import org.mabb.fontverter.FontNotSupportedException;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.validator.RuleValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class WoffFont implements FVFont {
    protected WoffHeader header;
    protected List<WoffTable> tables = new ArrayList<WoffTable>();
    protected List<FVFont> fonts = new ArrayList<FVFont>();

    public static WoffFont createBlankFont(int version) {
        WoffFont font;

        if (version == 1) {
            font = new Woff1Font();
            font.header = WoffHeader.createWoff1Header();
        } else {
            font = new Woff2Font();
            font.header = WoffHeader.createWoff2Header();
        }

        return font;
    }

    public WoffFont() {
    }

    public abstract WoffTable createTable();

    public abstract void addFontTable(byte[] unpaddedData, String tag, long checksum);

    public byte[] getData() throws IOException {
        // have to write out data twice for header calcs
        header.calculateValues(this);
        return getRawData();
    }

    byte[] getRawData() throws IOException {
        FontDataOutputStream out = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);

        Collections.sort(tables, new Comparator<WoffTable>() {
            public int compare(WoffTable o1, WoffTable o2) {
                String c1 = o1.getTag();
                String c2 = o2.getTag();
                return c1.compareTo(c2);
            }
        });

        out.write(header.getData());
        out.write(getTableDirectoryData());
        out.write(getCompressedDataBlock());

        return out.toByteArray();
    }

    byte[] getTableDirectoryData() throws IOException {
        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        for (WoffTable tableOn : tables)
            writer.write(tableOn.getDirectoryData());

        return writer.toByteArray();
    }

    byte[] getCompressedDataBlock() throws IOException {
        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        for (WoffTable tableOn : tables)
            writer.write(tableOn.getCompressedData());

        return writer.toByteArray();
    }

    public void read(byte[] fontFile) throws IOException {
        WoffParser parser = new WoffParser();
        parser.parse(fontFile, this);
    }

    public void addFont(FVFont adapter) {
        fonts.add(adapter);
    }

    public List<FVFont> getFonts() {
        return fonts;
    }

    public List<WoffTable> getTables() {
        return tables;
    }

    public int getCompressedSize() throws IOException {
        return getCompressedDataBlock().length;
    }

    public String getName() {
        if (fonts.size() == 0)
            return "Unknown Font Name";

        return fonts.get(0).getName();
    }

    public void normalize() {
    }

    public boolean doesPassStrictValidation() {
        return true;
    }

    public List<RuleValidator.FontValidatorError> getStrictValidationErrors() {
        return new ArrayList<RuleValidator.FontValidatorError>();
    }
}