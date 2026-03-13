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

package org.mabb.fontverter.opentype.TtfInstructions;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.Test;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OpenTypeParser;
import org.mabb.fontverter.opentype.TtfGlyph;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;
import org.mabb.fontverter.pdf.PdfFontExtractor;

import java.io.File;
import java.util.List;

import static org.mabb.fontverter.pdf.TestType0ToOpenTypeConverter.extractFont;


public class TestFullTtfPrograms {

    // Currentley only testing that real world full font programs execute without error in these tests
    @Test
    public void executeSecondGlyphIn_BrokenHelveticaNeueTtf() throws Exception {
        PDDocument doc = Loader.loadPDF(TestUtils.readTestFile("pdf/HorariosMadrid_Segovia.pdf"));

        PDFont rawType0Font = extractFont(doc, "TCQDAA+HelveticaNeue-Light-Identity-H");
        OpenTypeFont font = (OpenTypeFont) PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);

        List<TtfGlyph> glyphs = font.getGlyfTable().getNonEmptyGlyphs();
        TtfGlyph glyph = glyphs.get(1);
        List<TtfInstruction> instructions = glyph.getInstructions();

        TtfVirtualMachine vm = new TtfVirtualMachine(font);
        vm.execute(instructions);
    }

    @Test
    public void executeSecondGlyphIn_trebuchetMSTtf() throws Exception {
        OpenTypeParser parser = new OpenTypeParser();
        byte[] fontData = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "KJJTAM+TrebuchetMS.ttf"));
        OpenTypeFont font = parser.parse(fontData);

        List<TtfGlyph> glyphs = font.getGlyfTable().getNonEmptyGlyphs();
        TtfGlyph glyph = glyphs.get(1);
        List<TtfInstruction> instructions = glyph.getInstructions();

        TtfVirtualMachine vm = new TtfVirtualMachine(font);
        vm.execute(instructions);
    }

    @Test
    public void executeSecondGlyphIn_timetableTtf() throws Exception {
        OpenTypeParser parser = new OpenTypeParser();
        byte[] fontData = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf"));
        OpenTypeFont font = parser.parse(fontData);

        List<TtfGlyph> glyphs = font.getGlyfTable().getNonEmptyGlyphs();
        TtfGlyph glyph = glyphs.get(1);
        List<TtfInstruction> instructions = glyph.getInstructions();

        TtfVirtualMachine vm = new TtfVirtualMachine(font);
        vm.execute(instructions);
    }
}
