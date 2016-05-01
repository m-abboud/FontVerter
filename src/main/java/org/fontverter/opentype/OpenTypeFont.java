package org.fontverter.opentype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OpenTypeFont
{
    private List<OpenTypeTable> tables;
    private OpenTypeTable head;
    private HorizontalHeadTable hhea;
    private MaximumProfileTable mxap;
    private HorizontalMetricsTable hmtx;
    private OS2WindowsMetricsTable os2;
    private PostScriptTable post;
    private CmapTable cmap;
    private NameTable name;

    public static OpenTypeFont createBlankFont()
    {
        OpenTypeFont font = new OpenTypeFont();
        font.head = font.initTable(HeadTable.createEmptyTable());

        font.os2 = font.initTable(OS2WindowsMetricsTable.createEmptyTable());
        font.hhea = font.initTable(HorizontalHeadTable.createEmptyTable());
        font.mxap = font.initTable(MaximumProfileTable.createEmptyTable());
        font.hmtx = font.initTable(HorizontalMetricsTable.createEmptyTable());

        font.post = font.initTable(PostScriptTable.createEmptyTable());
        font.cmap = font.initTable(CmapTable.createEmptyTable());

        font.name = font.initTable(NameTable.createTable(new String[]{"", "Omsym2"}));

        return font;
    }

    public OpenTypeFont()
    {
        tables = new ArrayList<OpenTypeTable>();
    }

    private <T extends OpenTypeTable> T initTable(T table)
    {
        tables.add(table);
        return table;
    }

    public void addTable(OpenTypeTable table)
    {
        tables.add(table);
    }

    public List<OpenTypeTable> getTables()
    {
        // OpenType spec says tables must be sorted alphabetically
        Collections.sort(tables, new Comparator<OpenTypeTable>()
        {
            public int compare(OpenTypeTable left, OpenTypeTable right)
            {
                return left.getName().compareTo(right.getName());
            }
        });

        return tables;
    }
}
