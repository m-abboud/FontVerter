package org.fontverter.opentype.validator;

import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OtfNameConstants.RecordType;
import org.fontverter.validator.RuleValidator;
import org.fontverter.validator.ValidateRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenTypeFontValidator extends RuleValidator<OpenTypeFont> {
    public OpenTypeFontValidator() {
        addRuleDefinition(new NameTableRules());
        addRuleDefinition(new HorizontalHeadTableRules());
    }

    public static class HorizontalHeadTableRules {
        @ValidateRule(message = "Descender should be less than zero")
        public boolean descender(OpenTypeFont font) {
            return font.getHhea().descender < 0;
        }
    }

    public static class NameTableRules {
        @ValidateRule(message = "Version string does not match Open Type spec")
        public String versionStringSyntax(OpenTypeFont font) {
            String version = font.getName().getName(RecordType.VERSION_STRING);

            Matcher versionRegex = Pattern.compile("^Version [1-9][0-9]*[.][0-9]*").matcher(version);
            if (!versionRegex.matches())
                return version;

            return "";
        }
    }
}
