package org.mabb.fontverter.opentype;

import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.validator.RuleValidator;
import org.mabb.fontverter.validator.ValidateRule;
import org.mabb.fontverter.opentype.OtfNameConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mabb.fontverter.opentype.OtfNameConstants.*;
import static org.mabb.fontverter.validator.RuleValidator.ValidatorErrorType.*;

public class OpenTypeValidator extends RuleValidator<OpenTypeFont> {
    public OpenTypeValidator() {
        addRuleDefinition(new NameTableRules());
        addRuleDefinition(new HorizontalHeadTableRules());
        addRuleDefinition(new TableRules());
    }

    public static class TableRules {
        @ValidateRule(message = "OS/2 table should exist, TTFs/OTFs without one are considered invalid" +
                "by FireFox and Chrome")
        public boolean os2TableExists(OpenTypeFont font) {
            return font.getOs2() != null;
        }

        @ValidateRule(message = "Name table should exist, TTFs/OTFs without one are considered invalid" +
                "by FireFox and Chrome")
        public boolean nameTableExists(OpenTypeFont font) {
            return font.getNameTable() != null;
        }

        @ValidateRule(message = "PostScript table should exist, TTFs/OTFs without one are considered invalid" +
                "by FireFox and Chrome")
        public boolean postScriptTableExists(OpenTypeFont font) {
            return font.getPost() != null;
        }

        @ValidateRule(message = "cvt table should be even")
        public String cvtTableValueCountEven(OpenTypeFont font) {
            if (font.getCvt() == null)
                return "";

            int remainder = (int) (font.getCvt().record.length % 2);
            if (remainder != 0)
                return String.format("Read cvt table length is not divisible by 2. Length: %s",
                        font.getCvt().getValues().size());

            return "";
        }
    }

    public static class HorizontalHeadTableRules {
        @ValidateRule(message = "Descender should be less than zero", type = WARNING)
        public boolean descender(OpenTypeFont font) {
            return font.getHhea().descender < 0;
        }
    }

    public static class NameTableRules {
        @ValidateRule(message = "Version string does not match Open Type spec")
        public String versionStringSyntax(OpenTypeFont font) {
            if (font.getNameTable() == null)
                return "";

            String version = font.getNameTable().getName(RecordType.VERSION_STRING);
            if (version == null)
                return "";

            Matcher versionRegex = Pattern.compile("^Version [1-9][0-9]*[.][0-9]*").matcher(version);
            if (!versionRegex.matches())
                return version;

            return "";
        }
    }
}
