/*
 * Copyright (C) Maddie Abboud 2016
 *
 * FontVerter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FontVerter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FontVerter. If not, see <http://www.gnu.org/licenses/>.
 */

package org.mabb.fontverter.opentype;

import org.apache.commons.lang3.StringUtils;
import org.mabb.fontverter.*;
import org.mabb.fontverter.converter.*;
import org.mabb.fontverter.io.FontDataOutputStream;
import org.mabb.fontverter.validator.RuleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.mabb.fontverter.opentype.SfntHeader.*;

/**
 * OpenType covers both .otf and .ttfs , .otf is for CFF type fonts and .ttf is used for TrueType outline fonts.
 * Microsft's OpenType is built on type of the original apple TrueType spec
 * OpenType spec can be found here: https://www.microsoft.com/typography/otspec/otff.htm
 * Apple TrueType spec can be found here: https://developer.apple.com/fonts/TrueType-Reference-Manual
 */
public class OpenTypeFont implements FVFont {
    SfntHeader sfntHeader;
    private List<OpenTypeTable> tables;
    private static Logger log = LoggerFactory.getLogger(OpenTypeFont.class);
    private File sourceFile;

    public static OpenTypeFont createBlankFont() throws IOException {
        OpenTypeFont font = new OpenTypeFont();
        font.initTable(HeadTable.createDefaultTable());

        font.initTable(OS2WinMetricsTable.createDefaultTable());
        font.initTable(HorizontalHeadTable.createDefaultTable());
        font.initTable(MaximumProfileTable.createDefaultTable());

        font.initTable(PostScriptTable.createDefaultTable(3));
        font.initTable(CmapTable.createDefaultTable());
        font.initTable(HorizontalMetricsTable.createDefaultTable(font));

        font.initTable(NameTable.createDefaultTable());
        font.normalizeTables();
        return font;
    }

    public static OpenTypeFont createBlankTtfFont() throws IOException {
        OpenTypeFont font = new OpenTypeFont();
        font.initTable(HeadTable.createDefaultTable());

        font.initTable(OS2WinMetricsTable.createDefaultTable());
        font.initTable(HorizontalHeadTable.createDefaultTable());
        font.initTable(MaximumProfileTable.createDefaultV1Table());

        font.initTable(PostScriptTable.createDefaultTable(3));
        font.initTable(CmapTable.createDefaultTable());
        font.initTable(HorizontalMetricsTable.createDefaultTable(font));
        font.initTable(new ControlValueTable());
        font.initTable(new GlyphTable());
        font.initTable(new FontProgramTable());

        font.initTable(NameTable.createDefaultTable());
        font.normalizeTables();
        return font;
    }

    public OpenTypeFont() {
        tables = new ArrayList<OpenTypeTable>();
        sfntHeader = new SfntHeader();
    }

    public boolean detectFormat(byte[] fontFile) {
        String[] headerMagicNums = new String[]{CFF_FLAVOR, VERSION_1, VERSION_2, VERSION_2_5};
        for (String magicNumOn : headerMagicNums)
            if (FontVerterUtils.bytesStartsWith(fontFile, magicNumOn))
                return true;

        return false;
    }

    public void read(byte[] fontFile) throws IOException {
        try {
            new OpenTypeParser().parse(fontFile, this);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public String getName() {
        String name = getNameTable().getName(OtfNameConstants.RecordType.FULL_FONT_NAME);
        if (StringUtils.isEmpty(name) && isCffType()) {
            // sometimes name table won't have any valid entrys for CFF
            name = getCffTable().getCffFont().getFullName();
        }

        return name;
    }

    public boolean isValid() {
        return getValidationErrors().size() == 0;
    }

    public List<RuleValidator.FontValidatorError> getValidationErrors() {
        try {
            OpenTypeValidator validator = new OpenTypeValidator();
            return validator.validate(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            RuleValidator.FontValidatorError error = new RuleValidator.FontValidatorError(RuleValidator.ValidatorErrorType.ERROR,
                    String.format("Exception running validator: %s %s", ex.getMessage(), ex.getClass()));

            ArrayList<RuleValidator.FontValidatorError> errors = new ArrayList<RuleValidator.FontValidatorError>();
            errors.add(error);

            return errors;
        }
    }

    public void normalize() throws IOException {
        if (getOs2() == null)
            createNewOS2WinMetricsTable();

        if (getNameTable() == null)
            setName(NameTable.createDefaultTable());

        if (getPost() == null)
            setPost(PostScriptTable.createDefaultTable(getOpenTypeVersion()));

        finalizeFont();
    }

    private void createNewOS2WinMetricsTable() {
        HorizontalHeadTable hhea = getHhea();
        OS2WinMetricsTable table = OS2WinMetricsTable.createDefaultTable();
        setOs2(table);
    }

    public FontProperties getProperties() {
        FontProperties properties = new FontProperties();
        if (isCffType()) {
            properties.setMimeType("application/x-font-opentype");
            properties.setFileEnding("otf");
            properties.setCssFontFaceFormat("opentype");
        } else {
            properties.setMimeType("application/x-font-truetype");
            properties.setFileEnding("ttf");
            properties.setCssFontFaceFormat("truetype");
        }

        return properties;
    }

    public FontConverter createConverterForType(FontVerter.FontFormat fontFormat) throws FontNotSupportedException {
        switch (fontFormat) {
            case OTF:
                return new IdentityConverter();
            case WOFF1:
                return new OtfToWoffConverter();
            case WOFF2:
                return new OtfToWoffConverter.OtfToWoff2Converter();
            case EOT:
                return new OpenTypeToEotConverter();
            default:
                throw new FontNotSupportedException("Font conversion not supported");
        }
    }

    private <T extends OpenTypeTable> T initTable(T table) {
        table.font = this;
        tables.add(table);
        return table;
    }

    public void addTable(OpenTypeTable table) {
        tables.add(table);
    }

    private List<OpenTypeTable> descendingSortedTables() {
        // OpenType spec says tables must be sorted alphabetically, but then later states some very specific order
        // then still needs to be added here for seperate data entries
        Collections.sort(tables, new Comparator<OpenTypeTable>() {
            public int compare(OpenTypeTable left, OpenTypeTable right) {
                return left.getTableType().compareTo(right.getTableType());
            }
        });

        return tables;
    }

    public byte[] getData() throws IOException {
        // offsets and gotta calc checksums before doing final full font checksum so calling the data write out
        // twice to be lazy
        clearTableDataCache();
        finalizeFont();

        // now we for realsies write out the font bytes
        byte[] data = getRawData();
        clearTableDataCache();
        return data;
    }

    public void finalizeFont() throws IOException {
        // gott make sure checksums = 0 before doing calc
        for (OpenTypeTable tableOn : tables)
            tableOn.resetCalculations();

        descendingSortedTables();
        normalizeTables();
        calculateOffsets(tables);

        for (OpenTypeTable tableOn : tables)
            tableOn.finalizeRecord();

        // head checksum has to be very last after other checksums + offsets calculated so just grab full byte
        // output to calc instead of trying to re-edit the byte array at the right place
        getHead().clearDataCache();
        getHead().checksumAdjustment(getRawData());
    }

    private void normalizeTables() throws IOException {
        if (sfntHeader.sfntFlavor.isEmpty())
            sfntHeader.sfntFlavor = determineSfntFlavor();

        if (getCmap() != null && getMxap() != null && !getMxap().isFromParsedFont)
            getMxap().setNumGlyphs(getCmap().getGlyphCount());

        for (OpenTypeTable tableOn : tables) {
            tableOn.font = this;
            tableOn.normalize();
        }
    }

    private String determineSfntFlavor() {
        if (getCffTable() != null)
            return SfntHeader.CFF_FLAVOR;
        else if (getPost() != null)
            return SfntHeader.toVersionString(getPost().getVersion());

        return VERSION_1;
    }

    private byte[] getRawData() throws IOException {
        FontDataOutputStream out = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        sfntHeader.setNumTables(tables.size());

        out.write(sfntHeader.getData());

        for (OpenTypeTable tableOn : tables)
            out.write(tableOn.getRecordData());

        for (OpenTypeTable tableOn : tables)
            out.write(tableOn.getData());

        return out.toByteArray();
    }

    private void calculateOffsets(List<OpenTypeTable> tables) throws IOException {
        // must calculate table record offsets before we write any table data
        // start data offsets after sfnt header and table records
        int offset = tables.size() * OpenTypeTable.TABLE_RECORD_SIZE + SFNT_HEADER_SIZE;
        for (OpenTypeTable tableOn : tables) {
            tableOn.setOffset(offset);
            offset += tableOn.getData().length;
        }
    }

    public void orderTablesByDependencies() {
        Collections.sort(getTables(), new Comparator<OpenTypeTable>() {
            public int compare(OpenTypeTable o1, OpenTypeTable o2) {
                int order1 = dependencyOrderForTable(o1);
                int order2 = dependencyOrderForTable(o2);

                return order1 < order2 ? -1 : order1 == order2 ? 0 : 1;
            }
        });
    }

    private static int dependencyOrderForTable(OpenTypeTable table) {
        if (table instanceof HorizontalHeadTable)
            return 1;
        if (table instanceof HorizontalMetricsTable)
            return 2;
        if (table instanceof GlyphLocationTable)
            return 3;
        if (table instanceof GlyphTable)
            return 4;

        return 0;
    }

    // Should be called before/after font data generation. While building up the font generateData is called multiple
    // times to calculate offsets and checksums before writing out the full font.
    private void clearTableDataCache() {
        for (OpenTypeTable tableOn : tables)
            tableOn.clearDataCache();
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void removeTable(Class toRemoveType) {
        OpenTypeTable toRemoveTable = null;
        for (OpenTypeTable tableOn : tables) {
            if (tableOn.getClass() == toRemoveType)
                toRemoveTable = tableOn;
        }

        if (toRemoveTable != null)
            tables.remove(toRemoveTable);
    }

    private <T extends OpenTypeTable> T findTableType(Class type) {
        for (OpenTypeTable tableOn : tables) {
            if (tableOn.getClass() == type)
                return (T) tableOn;
        }

        return null;
    }

    private void setTable(OpenTypeTable toAdd) {
        removeTable(toAdd.getClass());
        tables.add(toAdd);
    }

    public boolean isCffType() {
        return sfntHeader.sfntFlavor.equals(CFF_FLAVOR);
    }

    public boolean isTrueTypeOutlineType() {
        return !sfntHeader.sfntFlavor.equals(CFF_FLAVOR);
    }

    public float getOpenTypeVersion() {
        return sfntHeader.openTypeVersion();
    }

    public HeadTable getHead() {
        return findTableType(HeadTable.class);
    }

    public void setHead(HeadTable head) {
        setTable(head);
    }

    public HorizontalHeadTable getHhea() {
        return findTableType(HorizontalHeadTable.class);
    }

    public void setHhea(HorizontalHeadTable hhea) {
        setTable(hhea);
    }

    public HorizontalMetricsTable getHmtx() {
        return findTableType(HorizontalMetricsTable.class);
    }

    public void setHmtx(HorizontalMetricsTable hmtx) {
        setTable(hmtx);
    }

    public OS2WinMetricsTable getOs2() {
        return findTableType(OS2WinMetricsTable.class);
    }

    public void setOs2(OS2WinMetricsTable os2) {
        setTable(os2);
    }

    public PostScriptTable getPost() {
        return findTableType(PostScriptTable.class);
    }

    public void setPost(PostScriptTable post) {
        setTable(post);
    }

    public CmapTable getCmap() {
        return findTableType(CmapTable.class);
    }

    public void setCmap(CmapTable cmap) {
        setTable(cmap);
    }

    public MaximumProfileTable getMxap() {
        return findTableType(MaximumProfileTable.class);
    }

    public void setMxap(MaximumProfileTable mxap) {
        setTable(mxap);
    }

    public ControlValueTable getCvt() {
        return findTableType(ControlValueTable.class);
    }

    public void setCvt(ControlValueTable cvt) {
        setTable(cvt);
    }

    public NameTable getNameTable() {
        return findTableType(NameTable.class);
    }

    public void setName(NameTable name) {
        setTable(name);
    }

    public CffTable getCffTable() {
        return findTableType(CffTable.class);
    }

    public GlyphLocationTable getLocaTable() {
        return findTableType(GlyphLocationTable.class);
    }

    public GlyphTable getGlyfTable() {
        return findTableType(GlyphTable.class);
    }

    public FontProgramTable getFpgmTable() { return findTableType(FontProgramTable.class); }

    public List<OpenTypeTable> getTables() {
        return tables;
    }

    public SfntHeader getSfntHeader() {
        return sfntHeader;
    }
}
