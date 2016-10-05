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

package org.mabb.fontverter.opentype;

public class OtfNameConstants {
    static final int WINDOWS_PLATFORM_ID = 3;
    static final int WINDOWS_DEFAULT_ENCODING = 1;

    static final int MAC_PLATFORM_ID = 1;
    static final int MAC_DEFAULT_ENCODING = 0;

    public enum WeightClass {
        THIN(100),
        EXTRA_LIGHT(200),
        LIGHT(300),
        NORMAL(400),
        MEDIUM(500),
        SEMI_BOLD(600),
        BOLD(700),
        EXTRA_BOLD(800),
        BLACK(900);

        private final int value;

        WeightClass(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static WeightClass fromInt(int i) {
            for (WeightClass typeOn : WeightClass.values())
                if (typeOn.getValue() == i) {
                    return typeOn;
                }

            return null;
        }
    }

    public enum WidthClass {
        ULTRA_CONDENSED(1, 50),
        EXTRA_CONDENSED(2, 62.5),
        CONDENSED(3, 75),
        SEMI_CONDENSED(4, 87.5),
        MEDIUM(5, 100),
        SEMI_EXPANDED(6, 112.5),
        EXPANDED(7, 125),
        EXTRA_EXPANDED(8, 150),
        ULTRA_EXPANDED(9, 200);

        private final int value;
        private final double percentNormal;

        WidthClass(int value, double percentNormal) {
            this.value = value;
            this.percentNormal = percentNormal;
        }

        public int getValue() {
            return value;
        }

        public double getPercentNormal() {
            return percentNormal;
        }

        public static WidthClass fromInt(int i) {
            for (WidthClass typeOn : WidthClass.values())
                if (typeOn.getValue() == i) {
                    return typeOn;
                }

            return null;
        }
    }

    public enum RecordType {
        COPYRIGHT(0),
        FONT_FAMILY(1),
        FONT_SUB_FAMILY(2),
        UNIQUE_FONT_ID(3),
        FULL_FONT_NAME(4),
        VERSION_STRING(5),
        POSTSCRIPT_NAME(6),
        TRADEMARK_NOTICE(7);

        private final int value;

        RecordType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static RecordType fromInt(int i) {
            for (RecordType typeOn : RecordType.values())
                if (typeOn.getValue() == i) {
                    return typeOn;
                }

            return null;
        }
    }

    public enum OtfEncodingType {
        SYMBOL(0),
        Unicode_BMP(1),
        ShiftJIS(2),
        PRC(3),
        BIG5(4),
        Wansung(5),
        Johab(6),
        Unicode_UCS4(10);

        private final int value;

        OtfEncodingType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static OtfEncodingType fromInt(int i) {
            for (OtfEncodingType typeOn : OtfEncodingType.values())
                if (typeOn.getValue() == i) {
                    return typeOn;
                }

            return null;
        }
    }

    public enum Language {
        UNITED_STATES(0x0409);

        private final int value;

        Language(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Language fromInt(int i) {
            for (Language typeOn : Language.values())
                if (typeOn.getValue() == i) {
                    return typeOn;
                }

            return null;
        }
    }

}
