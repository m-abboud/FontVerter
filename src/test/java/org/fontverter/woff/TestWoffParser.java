package org.fontverter.woff;

import org.apache.commons.io.FileUtils;
import org.fontverter.TestUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestWoffParser {
    @Test
    public void parseWoff1Header() throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File(TestUtils.TEST_PATH + "Open-Sans-WOFF-1.0.woff"));
        WoffParser parser = new WoffParser();
        parser.parse(data);
    }
}
