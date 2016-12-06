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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.Test;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.TtfGlyph;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;
import org.mabb.fontverter.pdf.PdfFontExtractor;

import java.util.List;

import static org.mabb.fontverter.pdf.TestType0ToOpenTypeConverter.extractFont;

public class TestVmOnFullPrograms {
    @Test
    public void given_type0_withCFF_HelveticaNeueBug() throws Exception {
        PDDocument doc = PDDocument.load(TestUtils.readTestFile("pdf/HorariosMadrid_Segovia.pdf"));

        PDFont rawType0Font = extractFont(doc, "TCQDAA+HelveticaNeue-Light-Identity-H");
        OpenTypeFont font = (OpenTypeFont) PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);

        List<TtfGlyph> glyphs = font.getGlyfTable().getNonEmptyGlyphs();
        TtfGlyph glyph = glyphs.get(1);
        List<TtfInstruction> instructions = glyph.getInstructions();

        TtfVirtualMachine vm = new TtfVirtualMachine(font);
        vm.execute(instructions);
    }
}
