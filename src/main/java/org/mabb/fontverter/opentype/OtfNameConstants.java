package org.mabb.fontverter.opentype;

public class OtfNameConstants {
    static final int WINDOWS_PLATFORM_ID = 3;
    static final int WINDOWS_ENCODING = 1;

    static final int MAC_PLATFORM_ID = 1;
    static final int MAC_ENCODING = 0;

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
