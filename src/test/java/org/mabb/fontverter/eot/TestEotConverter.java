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
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestUtils;
import org.mabb.fontverter.opentype.OpenTypeFont;

import java.io.File;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class TestEotConverter {
    @Test
    public void givenUnCompressedEotFont_whenConvertedToOpenType_thenOpenTypeFontValid() throws Exception {
        byte[] data = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "/eot/arial.eot"));

        EotFont eotFont = new EotFont();
        eotFont.read(data);

        FVFont converted = FontVerter.convertFont(eotFont, FontVerter.FontFormat.OTF);
        Assert.assertTrue(converted.isValid());
        Assert.assertThat(converted, instanceOf(OpenTypeFont.class));
    }

}
