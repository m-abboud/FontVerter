package org.fontverter.woff;

import org.fontverter.validator.RuleValidator;
import org.fontverter.validator.ValidateRule;

import java.io.IOException;

import static org.fontverter.woff.Woff2Font.Woff2Table;

public class Woff2Validator extends RuleValidator<Woff2Font> {
    public Woff2Validator() {
        addRuleDefinition(new HeaderRules());
    }

    public static class HeaderRules {
        @ValidateRule(message = "total sfnt size figure sketchy")
        public String totalSfntSize(Woff2Font font) throws IOException {
            // fixme should be done by rebuilding an OpenTypeFont with all the tables
            if (font.header.totalSfntSize < font.header.totalCompressedSize)
                return String.format("sfnt size: %d, compressed size: %d",
                        font.header.totalSfntSize,
                        font.header.totalCompressedSize);
            return "";
        }

        @ValidateRule(message = "header is not correct size")
        public String headerBytesCorrectSize(Woff2Font font) throws IOException {
            if (font.header.getData().length != 48)
                return String.format("%d != 48", font.header.getData().length);
            return "";
        }
    }
}
