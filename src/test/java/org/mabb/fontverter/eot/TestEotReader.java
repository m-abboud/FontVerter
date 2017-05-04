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

//    @Test
//    public void givenEotFont_whenHeaderRead_thenHeaderHasStringValues() throws Exception {
//        byte[] data = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "/eot/arial.eot"));
//
//        EotFont font = new EotFont();
//        font.read(data);
//
//        Assert.assertEquals("", font.getHeader().getFamilyName());
//    }

}
