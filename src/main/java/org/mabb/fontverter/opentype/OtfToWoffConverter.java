package org.mabb.fontverter.opentype;

import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontConverter;
import org.mabb.fontverter.woff.WoffConstants.TableFlagType;
import org.mabb.fontverter.woff.WoffFont;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OtfToWoffConverter implements FontConverter {
    OpenTypeFont otfFont;
    private WoffFont woffFont;
    protected int woffVersion = 1;

    private static Map<Class, TableFlagType> tablesToFlags = new ConcurrentHashMap<Class, TableFlagType>();

    static {
        tablesToFlags.put(CffTable.class, TableFlagType.CFF);
        tablesToFlags.put(NameTable.class, TableFlagType.name);
        tablesToFlags.put(HeadTable.class, TableFlagType.head);
        tablesToFlags.put(CmapTable.class, TableFlagType.cmap);
        tablesToFlags.put(HorizontalHeadTable.class, TableFlagType.hhea);
        tablesToFlags.put(HorizontalMetricsTable.class, TableFlagType.hmtx);
        tablesToFlags.put(OS2WinMetricsTable.class, TableFlagType.OS2);
        tablesToFlags.put(MaximumProfileTable.class, TableFlagType.maxp);
        tablesToFlags.put(PostScriptTable.class, TableFlagType.post);
    }

    public OtfToWoffConverter() {
    }

    public FVFont convertFont(FVFont font) throws IOException {
        otfFont = ((OtfFontAdapter) font).getUnderlyingFont();
        woffFont = WoffFont.createBlankFont(woffVersion);
        woffFont.addFont(font);
        addFontTables();

        return woffFont;
    }

    private void addFontTables() throws IOException {
        for (OpenTypeTable tableOn : otfFont.tables)
            woffFont.addFontTable(tableOn.getUnpaddedData(), getTableFlag(tableOn), tableOn.getChecksum());
    }

    private TableFlagType getTableFlag(OpenTypeTable table) {
        if (tablesToFlags.containsKey(table.getClass()))
            return tablesToFlags.get(table.getClass());

        return TableFlagType.arbitrary;
    }

    public static class OtfToWoff2Converter extends OtfToWoffConverter {
        public OtfToWoff2Converter() {
            woffVersion = 2;
        }
    }
}
