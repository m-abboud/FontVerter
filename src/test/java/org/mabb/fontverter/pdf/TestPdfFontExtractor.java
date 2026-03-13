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

package org.mabb.fontverter.pdf;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mabb.fontverter.FVFont;
import org.mabb.fontverter.FontVerter;
import org.mabb.fontverter.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestPdfFontExtractor {
    @Test
    public void givenCorruptOverlyLargeOpenTypeTableLength_doesNotThrowOomError() throws IOException {
        PDDocument doc = Loader.loadPDF(
                TestUtils.readTestFile("pdf/corrupt_overly_large_opentype_table_length.pdf"));
        PdfFontExtractor extractor = new PdfFontExtractor();

        List<FVFont> fonts = extractor.extractToFVFonts(doc);

        Assert.assertEquals(40, fonts.size());
        doc.close();
    }

    @Test
    public void givenPdfWith2Fonts_extractFontsToFVFontList_thenListHasSameNumberOfFonts() throws IOException {
        PDDocument doc = Loader.loadPDF(TestUtils.readTestFile("pdf/brno30.pdf"));
        PdfFontExtractor extractor = new PdfFontExtractor();

        List<FVFont> fonts = extractor.extractToFVFonts(doc);

        Assert.assertEquals(3, fonts.size());
        doc.close();
    }

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void givenPdfWith2Fonts_extractFontsToDir_thenDirectoryHasThreeTtfFiles() throws IOException {
        PDDocument doc = Loader.loadPDF(TestUtils.readTestFile("pdf/brno30.pdf"));
        PdfFontExtractor extractor = new PdfFontExtractor();

        File extractDir = folder.getRoot();
        extractor.extractFontsToDir(doc, extractDir);
        File[] fontFiles = extractDir.listFiles();

        Assert.assertEquals(3, fontFiles.length);
        for (File fileOn : fontFiles)
            Assert.assertEquals("ttf", FilenameUtils.getExtension(fileOn.getPath()));

        doc.close();
    }

    @Test
    public void givenPdfWith2Fonts_extractFontsToDirWithWoff1FormatSet_thenDirectoryHasThreeWoffFiles() throws IOException {
        File extractDir = folder.getRoot();
        PDDocument doc = Loader.loadPDF(TestUtils.readTestFile("pdf/brno30.pdf"));

        PdfFontExtractor extractor = new PdfFontExtractor();
        extractor.setExtractFormat(FontVerter.FontFormat.WOFF1);

        extractor.extractFontsToDir(doc, extractDir);
        File[] fontFiles = extractDir.listFiles();

        Assert.assertEquals(3, fontFiles.length);
        for (File fileOn : fontFiles)
            Assert.assertEquals("woff", FilenameUtils.getExtension(fileOn.getPath()));

        doc.close();
    }
}
