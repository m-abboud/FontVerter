/*
 * Copyright (C) Matthew Abboud 2016
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

import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.validator.RuleValidator;
import org.mabb.fontverter.validator.ValidateRule;

import java.io.IOException;

public class Woff2Validator extends RuleValidator<Woff2Font> {
    public Woff2Validator() {
        addRuleDefinition(new HeaderRules());
    }

    public static class HeaderRules {
        @ValidateRule(message = "Reported total sfnt size not equal to calculated size")
        public String totalSfntSize(Woff2Font font) throws IOException {
            OpenTypeFont otfFOnt = ((OpenTypeFont) font.getFonts().get(0));
            int reportedTotal = otfFOnt.getData().length;
            int total = 12 + (font.getTables().size() * 16);

            for (WoffTable tableOn : font.getTables())
                total += round4(tableOn.originalLength);
            if (reportedTotal != total)
                return String.format("reported: %d != calc: %d", reportedTotal, total);
            return "";
        }

        private int round4(int num) {
            if (num % 4 == 0)
                return num;
            return num + (4 - (num % 4));
        }

        @ValidateRule(message = "Header is not correct size")
        public String headerBytesCorrectSize(Woff2Font font) throws IOException {
            if (font.header.getData().length != 48)
                return String.format("%d != 48", font.header.getData().length);
            return "";
        }
    }
}
