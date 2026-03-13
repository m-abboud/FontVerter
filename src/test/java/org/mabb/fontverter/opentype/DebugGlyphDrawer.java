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

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.junit.Test;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.opentype.TtfInstructions.instructions.TtfInstruction;
import org.mabb.fontverter.pdf.PdfFontExtractor;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mabb.fontverter.pdf.TestType0ToOpenTypeConverter.extractFont;

public class DebugGlyphDrawer {
    public static void drawGlyph(TtfGlyph glyph) throws IOException {
        BufferedImage image = new BufferedImage(650, 650, BufferedImage.TYPE_INT_RGB);
        Graphics2D gfx = image.createGraphics();

        gfx.translate(0, 300);
        gfx.scale(1, -1);

        gfx.setColor(Color.white);
        gfx.fillRect(0, -1000, 2060, 2060);
        gfx.setColor(Color.lightGray);
        gfx.translate(100, 50);
        gfx.fillRect(0, 0, 1000, 1000);
        gfx.setColor(Color.BLACK);

        gfx.scale(.05, .05);

//        gfx.rotate(Math.toRadians(180));
//        gfx.translate(-2200, -2200);

        Color[] colors = new Color[]{Color.BLACK, Color.MAGENTA, Color.GREEN, Color.BLUE, Color.cyan};
        java.util.List<Path2D.Double> paths = glyph.getPaths();
        for (int i = 0; i < paths.size(); i++) {
            Path2D pathOn = paths.get(i);
            gfx.setColor(colors[i]);

            gfx.draw(pathOn);
        }

        gfx.dispose();

//        ImageIO.write(image, "jpg", new File("test.jpg"));
    }


    @Test
    public void given_type0_withCFF_HelveticaNeueBug() throws Exception {
        PDDocument doc = Loader.loadPDF(TestUtils.readTestFile("pdf/HorariosMadrid_Segovia.pdf"));

        PDFont rawType0Font = extractFont(doc, "TCQDAA+HelveticaNeue-Light-Identity-H");
        OpenTypeFont font = (OpenTypeFont) PdfFontExtractor.convertType0FontToOpenType((PDType0Font) rawType0Font);
        TestUtils.saveTempFile(font.getData(), "TCQDAA+HelveticaNeue-Light-Identity-H.ttf");

        FileUtils.writeByteArrayToFile(new File("C:/projects/Pdf2Dom/fontTest/TCQDAA+HelveticaNeue-Light-Identity-H.ttf"),
                font.getData());
        List<TtfGlyph> glyphs = font.getGlyfTable().getNonEmptyGlyphs();
        TtfGlyph glyph = glyphs.get(1);
        List<TtfInstruction> instructions = glyph.getInstructions();

        DebugGlyphDrawer.drawGlyph(glyph);
    }

}
