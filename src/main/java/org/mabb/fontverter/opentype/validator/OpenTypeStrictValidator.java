package org.mabb.fontverter.opentype.validator;

import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.validator.ValidateRule;

public class OpenTypeStrictValidator extends OpenTypeFontValidator {
    public OpenTypeStrictValidator() {
        super();
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
    }

}
