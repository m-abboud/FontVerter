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

package org.mabb.fontverter.eot;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mabb.fontverter.FontProperties;
import org.mabb.fontverter.TestUtils;

import java.io.File;

public class TestEotReader {
    @Test
    public void givenEotFont_whenHeaderRead_thenHeaderObjHasVersion() throws Exception {
        byte[] data = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "/eot/arial.eot"));

        EotFont font = new EotFont();
        font.read(data);

        Assert.assertEquals(EotHeader.VERSION_TWO, font.getHeader().version);
    }

    @Test
    public void givenEotFontUncompressedEmbeddedFont_whenRead_thenEmbeddedFontIsValid() throws Exception {
        byte[] data = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "/eot/arial.eot"));

        EotFont font = new EotFont();
        font.read(data);

        Assert.assertEquals(25, font.getEmbeddedFont().getSfntHeader().numTables);
        Assert.assertTrue(font.isValid());
    }

    @Test
    public void givenEotFont_whenRead_thenFontPropertiesNamesCorrect() throws Exception {
        byte[] data = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "/eot/fontverterfullalphabetfont-webfont.eot"));

        EotFont font = new EotFont();
        font.read(data);
        FontProperties properties = font.getProperties();

        Assert.assertEquals(properties.getFullName(), "FontVerter+FullAlphabetFont");
        Assert.assertEquals(properties.getName(), "FontVerter+FullAlphabetFont");
        Assert.assertEquals(properties.getWeight(), "Medium");
        Assert.assertEquals(properties.getVersion(), "Version 1.000");
        Assert.assertEquals(properties.getFamily(), "FullAlphabetFont");
    }

}
