/*
 * Copyright (C) Matthew Abboud 2016
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

import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestUtils;

import java.awt.geom.Point2D;
import java.util.List;

import static org.hamcrest.Matchers.lessThan;

public class TestTrueTypeGlyphs {
    @Test
    public void givenTTF_whenParsed_thenLocaGlyphOffsets_equalsNumGlyphs() throws Exception {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        GlyphLocationTable table = font.getLocaTable();

        // -1 for last offset that ends the last glyph length calc
        Assert.assertEquals(font.getMxap().getNumGlyphs(), table.getOffsets().length - 1);
    }

    @Test
    public void given_TTF_whenParsed_thenGlyphTable_glyphCount_equalsMaxpNumGlyphsMinusBlankGlyphs() throws Exception {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        GlyphTable table = font.getGlyfTable();

        Assert.assertEquals(88, table.glyphs.size());
    }

    @Test
    public void parseTtf_thenGlyphBoundingBox_parsedCorrectly() throws Exception {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        TtfGlyph glyph = font.getGlyfTable().glyphs.get(5);

        Assert.assertEquals(86, glyph.xMin);
        Assert.assertEquals(986, glyph.xMax);

        Assert.assertEquals(20, glyph.yMin);
        Assert.assertEquals(1400, glyph.yMax);
    }

    @Test
    public void parseTtf_then_parsedCoordinatesForGlyph_isSameSize() throws Exception {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");
        TtfGlyph glyph = font.getGlyfTable().glyphs.get(5);
        List<Point2D.Double> coords = glyph.getCoordinates();

        Assert.assertEquals(12, coords.size());
    }

    @Test
    public void parseTtf_then_parsedGlyphCoordinates_allWithinNormalRange() throws Exception {
        OpenTypeFont font = (OpenTypeFont) FontVerter.readFont(TestUtils.TEST_PATH + "ttf/GKQXJT+Timetable.ttf");

        List<TtfGlyph> glyphs = font.getGlyfTable().getGlyphs();
        for (TtfGlyph glyphOn : glyphs)
            validateGlyphCoordinateRanges(font, glyphOn);
    }

    private void validateGlyphCoordinateRanges(OpenTypeFont font, TtfGlyph glyphOn) {
        List<TtfGlyph> glyphs = font.getGlyfTable().getGlyphs();
        int index = glyphs.indexOf(glyphOn);
        int coordCount = glyphs.size();

        List<Point2D.Double> coords = glyphOn.getCoordinates();
        for (Point2D.Double coordOn : coords) {
            int coordIndex = coords.indexOf(coordOn);

            String message = String.format("GlyphIndex:'%d'  Coord Index:'%d'  Coord Count:'%d'", index, coordCount, coordIndex);

            Assert.assertThat(message, Math.abs(coordOn.x), lessThan(3000D));
            Assert.assertThat(message, Math.abs(coordOn.y), lessThan(3000D));
        }
    }
}
