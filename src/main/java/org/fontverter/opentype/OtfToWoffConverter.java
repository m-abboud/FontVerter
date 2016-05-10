package org.fontverter.opentype;

import org.fontverter.FontAdapter;
import org.fontverter.FontConverter;
import org.fontverter.woff.Woff1Font;
import org.fontverter.woff.WoffConstants.TableFlagType;
import org.fontverter.woff.WoffFont;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OtfToWoffConverter implements FontConverter {
    OpenTypeFont otfFont;
    private WoffFont woffFont;

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

    public FontAdapter convertFont(FontAdapter font) throws IOException {
        otfFont = ((OtfFontAdapter)font).getFont();
        woffFont = Woff1Font.createBlankFont();
        woffFont.addFont(font);
        addFontTables();

        return woffFont;
    }

    private void addFontTables() throws IOException {
        for(OpenTypeTable tableOn : otfFont.tables)
            woffFont.addFontTable(tableOn.getUnpaddedData(), getTableFlag(tableOn), tableOn.getChecksum());
//        woffFont.addFontTable(otfFont.createSfntHeader(), TableFlagType.s);
    }

    private TableFlagType getTableFlag(OpenTypeTable table) {
        if (tablesToFlags.containsKey(table.getClass()))
            return tablesToFlags.get(table.getClass());

        return TableFlagType.arbitrary;
    }
}
