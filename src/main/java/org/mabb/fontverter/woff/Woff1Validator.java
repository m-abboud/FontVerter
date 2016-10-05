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

package org.mabb.fontverter.woff;

import org.mabb.fontverter.validator.RuleValidator;
import org.mabb.fontverter.validator.ValidateRule;
import org.mabb.fontverter.woff.Woff1Font.Woff1Table;

import java.io.IOException;

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
                    overlapErrors += String.format("\n Table %s %d != %d", tableOn.getTag(), tableOn.offset, positionOn);

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
                    paddingErrors += String.format("\n Table %s %d %% 4 != 0", tableOn.getTag(), tableOn.offset);
            }

            return paddingErrors;
        }
    }
}
