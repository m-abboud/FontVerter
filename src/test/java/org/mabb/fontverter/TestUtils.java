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

package org.mabb.fontverter;

import org.apache.commons.io.FileUtils;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OpenTypeValidator;

import java.io.File;
import java.io.IOException;

public class TestUtils {
    public static final String TEST_PATH = "src/test/files/";
    public static final String tempOutputPath = "src/test/test-output/";

    public static void runAllOtfValidators(OpenTypeFont font) throws Exception {
        runFontVerterInternalValidator(font);

        try {
            fontboxValidate(font.getData());
        } catch (IOException ex) {
            // fontbox bug location table is not mandatory for otf with cff type fonts
            // putting patch in soon
            if (!ex.getMessage().contains("loca is mandatory"))
                throw ex;
        }
    }

    public static void runFontVerterInternalValidator(OpenTypeFont font) throws Exception {
        OpenTypeValidator validator = new OpenTypeValidator();
        validator.validateWithExceptionsThrown(font);
    }

    public static void fontboxValidate(byte[] file) throws IOException {
        // fontbox for validating generated fonts, fontbox has good pdf type font parsing no generation tho
        // but font classes have package local constructors
        OTFParser parser = new OTFParser();
        org.apache.fontbox.ttf.OpenTypeFont font = parser.parse(new RandomAccessReadBuffer(file));
        font.getName();
    }

    public static byte[] readTestFile(String filePath) throws IOException {
        return FileUtils.readFileToByteArray(new File(TEST_PATH + filePath));
    }

    public static void saveTempFile(byte[] data, String fileName) throws Exception {
        File outputFile = new File(tempOutputPath + fileName);
        if (outputFile.exists())
            outputFile.delete();

        FileUtils.writeByteArrayToFile(outputFile, data);
    }
}
