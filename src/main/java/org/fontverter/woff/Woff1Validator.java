package org.fontverter.woff;

import org.fontverter.opentype.OpenTypeFont;
import org.fontverter.opentype.OtfNameConstants.RecordType;
import org.fontverter.validator.RuleValidator;
import org.fontverter.validator.ValidateRule;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fontverter.woff.Woff1Font.*;
import static org.fontverter.woff.Woff1Font.Woff1Table.WOFF1_TABLE_DIRECTORY_ENTRY_SIZE;

public class Woff1Validator extends RuleValidator<Woff1Font> {
    public Woff1Validator() {
        addRuleDefinition(new TableDirectoryRules());
    }

    public static class TableDirectoryRules {
        @ValidateRule(message = "Table offsets are overlapping")
        public String offsetsOverlapping(Woff1Font font) throws IOException {
            String overlapErrors = "";
            int positionOn = font.tableDirectoryOffsetStart();

            for (WoffTable table : font.tables) {
                Woff1Table tableOn = ((Woff1Table) table);
                if (tableOn.offset != positionOn)
                    overlapErrors += String.format("\n Table %s %d != %d", tableOn.flag, tableOn.offset, positionOn);

                positionOn += tableOn.getCompressedData().length;
            }

            return overlapErrors;
        }

        @ValidateRule(message = "Table offsets are not divisible by four")
        public String tablesArePaddedCorrectly(Woff1Font font) {
            String paddingErrors = "";

            for (WoffTable table : font.tables) {
                Woff1Table tableOn = ((Woff1Table) table);
                if (tableOn.offset % 4 != 0)
                    paddingErrors += String.format("\n Table %s %d %% 4 != 0", tableOn.flag, tableOn.offset);
            }

            return paddingErrors;
        }
    }
}
