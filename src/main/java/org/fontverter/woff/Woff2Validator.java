package org.fontverter.woff;

import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OtfFontAdapter;
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
            OpenTypeFont otfFOnt= ((OtfFontAdapter) font.getFonts().get(0)).getFont();
            int reportedTotal = otfFOnt.getFontData().length;
            int total = 12 + (font.getTables().size() * 16);

            for(WoffTable tableOn : font.getTables() )
                total += round4(tableOn.originalLength);
            if(reportedTotal != total)
                return String.format("reported: %d != calc: %d", reportedTotal, total);
            return "";
        }
        private int round4(int num){
            if(num % 4 == 0)
                return num;
            return num + (4 - (num %4));
        }

        @ValidateRule(message = "header is not correct size")
        public String headerBytesCorrectSize(Woff2Font font) throws IOException {
            if (font.header.getData().length != 48)
                return String.format("%d != 48", font.header.getData().length);
            return "";
        }
    }
}
