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

package org.mabb.fontverter.opentype;

import java.util.ArrayList;
import java.util.List;

public class CodePageRange {
    public static List<CodePageRange> CODE_PAGE_RANGES = new ArrayList<CodePageRange>();
    public final int bit;
    public final int codePage;
    public final String name;

    public CodePageRange(int bit, int codePage, String name) {
        this.bit = bit;
        this.codePage = codePage;
        this.name = name;
        CODE_PAGE_RANGES.add(this);
    }

    public static final CodePageRange LATIN_1 = new CodePageRange(0, 1252, "Latin 1");
    public static final CodePageRange LATIN_2_EASTERN_EUROPE = new CodePageRange(1, 1250, "Latin 2: Eastern Europe");
    public static final CodePageRange CYRILLIC = new CodePageRange(2, 1251, "Cyrillic");
    public static final CodePageRange GREEK = new CodePageRange(3, 1253, "Greek");
    public static final CodePageRange TURKISH = new CodePageRange(4, 1254, "Turkish");
    public static final CodePageRange HEBREW = new CodePageRange(5, 1255, "Hebrew");
    public static final CodePageRange ARABIC = new CodePageRange(6, 1256, "Arabic");
    public static final CodePageRange WINDOWS_BALTIC = new CodePageRange(7, 1257, "Windows Baltic");
    public static final CodePageRange VIETNAMESE = new CodePageRange(8, 1258, "	Vietnamese");
    public static final CodePageRange THAI = new CodePageRange(16, 874, "Thai");
    public static final CodePageRange JIS_JAPAN = new CodePageRange(17, 932, "JIS/Japan");
    public static final CodePageRange CHINESE_SIMPLIFIED_CHARS__PRC_AND_SINGAPORE =
            new CodePageRange(18, 936, "Chinese: Simplified chars--PRC and Singapore");

    public static final CodePageRange KOREAN_WANSUNG = new CodePageRange(19, 949, "Korean Wansung");
    public static final CodePageRange CHINESE_TRADITIONAL_CHARS__TAIWAN_AND_HONG_KONG =
            new CodePageRange(20, 950, "Chinese: Traditional chars--Taiwan and Hong Kong");

    public static final CodePageRange KOREAN_JOHAB = new CodePageRange(21, 1361, "	Korean Johab");
    public static final CodePageRange MACINTOSH_CHARACTER_SET_US_ROMAN = new CodePageRange(29, 0, " Macintosh Character Set (US Roman)");
    public static final CodePageRange OEM_CHARACTER_SET = new CodePageRange(30, 0, "OEM Character Set");
    public static final CodePageRange SYMBOL_CHARACTER_SET = new CodePageRange(31, 0, "Symbol Character Set");
    public static final CodePageRange IBM_GREEK = new CodePageRange(48, 869, "IBM Greek");
    public static final CodePageRange MS_DOS_RUSSIAN = new CodePageRange(49, 866, "MS-DOS Russian");
    public static final CodePageRange MS_DOS_NORDIC = new CodePageRange(50, 865, "MS-DOS Nordic");
    public static final CodePageRange ARABIC2 = new CodePageRange(51, 864, "Arabic");
    public static final CodePageRange MS_DOS_CANADIAN_FRENCH = new CodePageRange(52, 863, "MS-DOS Canadian French");
    public static final CodePageRange HEBREW2 = new CodePageRange(53, 862, "Hebrew");
    public static final CodePageRange MS_DOS_ICELANDIC = new CodePageRange(54, 861, "MS-DOS Icelandic");
    public static final CodePageRange MS_DOS_PORTUGUESE = new CodePageRange(55, 860, "MS-DOS Portuguese");
    public static final CodePageRange IBM_TURKISH = new CodePageRange(56, 857, "IBM Turkish");
    public static final CodePageRange IBM_CYRILLIC_PRIMARILY_RUSSIAN = new CodePageRange(57, 855, "IBM Cyrillic; primarily Russian");
    public static final CodePageRange LATIN_2 = new CodePageRange(58, 852, "Latin 2");
    public static final CodePageRange MS_DOS_BALTIC = new CodePageRange(59, 775, "MS-DOS Baltic");
    public static final CodePageRange GREEK_FORMER_437_G = new CodePageRange(60, 737, "Greek; former 437 G");
    public static final CodePageRange ARABIC_ASMO_708 = new CodePageRange(61, 708, "Arabic; ASMO 708");
    public static final CodePageRange WE_LATIN_1 = new CodePageRange(62, 850, "WE/Latin 1");
    public static final CodePageRange US = new CodePageRange(63, 437, "US");

    public static class OtfUnicodeRange {
        public static List<OtfUnicodeRange> UNICODE_RANGES = new ArrayList<OtfUnicodeRange>();

        public final int bit;
        public final String name;

        public OtfUnicodeRange(int bit, String name) {
            this.bit = bit;
            this.name = name;
            UNICODE_RANGES.add(this);
        }

        public static final OtfUnicodeRange BASIC_LATIN = new OtfUnicodeRange(0, "Basic Latin");
        public static final OtfUnicodeRange LATIN_1_SUPPLEMENT = new OtfUnicodeRange(1, "Latin-1 Supplement");
        public static final OtfUnicodeRange LATIN_EXTENDED_A = new OtfUnicodeRange(2, "Latin Extended-A");
        public static final OtfUnicodeRange LATIN_EXTENDED_B = new OtfUnicodeRange(3, "Latin Extended-B");
        public static final OtfUnicodeRange IPA_EXTENSIONS = new OtfUnicodeRange(4, "IPA Extensions");
        public static final OtfUnicodeRange SPACING_MODIFIER_LETTERS = new OtfUnicodeRange(5, "Spacing Modifier Letters");
        public static final OtfUnicodeRange COMBINING_DIACRITICAL_MARKS = new OtfUnicodeRange(6, "Combining Diacritical Marks");
        public static final OtfUnicodeRange GREEK_AND_COPTIC = new OtfUnicodeRange(7, "Greek and Coptic");
        public static final OtfUnicodeRange COPTIC = new OtfUnicodeRange(8, "Coptic");
        public static final OtfUnicodeRange CYRILLIC = new OtfUnicodeRange(9, "Cyrillic");
        public static final OtfUnicodeRange ARMENIAN = new OtfUnicodeRange(10, "Armenian");
        public static final OtfUnicodeRange HEBREW = new OtfUnicodeRange(11, "Hebrew");
        public static final OtfUnicodeRange VAI = new OtfUnicodeRange(12, "Vai");
        public static final OtfUnicodeRange ARABIC = new OtfUnicodeRange(13, "Arabic");
        public static final OtfUnicodeRange NKO = new OtfUnicodeRange(14, "NKo");
        public static final OtfUnicodeRange DEVANAGARI = new OtfUnicodeRange(15, "Devanagari");
        public static final OtfUnicodeRange BENGALI = new OtfUnicodeRange(16, "Bengali");
        public static final OtfUnicodeRange GURMUKHI = new OtfUnicodeRange(17, "Gurmukhi");
        public static final OtfUnicodeRange GUJARATI = new OtfUnicodeRange(18, "Gujarati");
        public static final OtfUnicodeRange ORIYA = new OtfUnicodeRange(19, "Oriya");
        public static final OtfUnicodeRange TAMIL = new OtfUnicodeRange(20, "Tamil");
        public static final OtfUnicodeRange TELUGU = new OtfUnicodeRange(21, "Telugu");
        public static final OtfUnicodeRange KANNADA = new OtfUnicodeRange(22, "Kannada");
        public static final OtfUnicodeRange MALAYALAM = new OtfUnicodeRange(23, "Malayalam");
        public static final OtfUnicodeRange THAI = new OtfUnicodeRange(24, "Thai");
        public static final OtfUnicodeRange LAO = new OtfUnicodeRange(25, "Lao");
        public static final OtfUnicodeRange GEORGIAN = new OtfUnicodeRange(26, "Georgian");
        public static final OtfUnicodeRange BALINESE = new OtfUnicodeRange(27, "Balinese");
        public static final OtfUnicodeRange HANGUL_JAMO = new OtfUnicodeRange(28, "Hangul Jamo");
        public static final OtfUnicodeRange LATIN_EXTENDED_ADDITIONAL = new OtfUnicodeRange(29, "Latin Extended Additional");
        public static final OtfUnicodeRange GREEK_EXTENDED = new OtfUnicodeRange(30, "Greek Extended");
        public static final OtfUnicodeRange GENERAL_PUNCTUATION = new OtfUnicodeRange(31, "General Punctuation");
        public static final OtfUnicodeRange SUPERSCRIPTS_AND_SUBSCRIPTS = new OtfUnicodeRange(32, "Superscripts And Subscripts");
        public static final OtfUnicodeRange CURRENCY_SYMBOLS = new OtfUnicodeRange(33, "Currency Symbols");
        public static final OtfUnicodeRange COMBINING_DIACRITICAL_MARKS_FOR_SYMBOLS = new OtfUnicodeRange(34, "Combining Diacritical Marks For Symbols");
        public static final OtfUnicodeRange LETTERLIKE_SYMBOLS = new OtfUnicodeRange(35, "Letterlike Symbols");
        public static final OtfUnicodeRange NUMBER_FORMS = new OtfUnicodeRange(36, "Number Forms");
        public static final OtfUnicodeRange ARROWS = new OtfUnicodeRange(37, "Arrows");
        public static final OtfUnicodeRange MATHEMATICAL_OPERATORS = new OtfUnicodeRange(38, "Mathematical Operators");
        public static final OtfUnicodeRange MISCELLANEOUS_TECHNICAL = new OtfUnicodeRange(39, "Miscellaneous Technical");
        public static final OtfUnicodeRange CONTROL_PICTURES = new OtfUnicodeRange(40, "Control Pictures");
        public static final OtfUnicodeRange OPTICAL_CHARACTER_RECOGNITION = new OtfUnicodeRange(41, "Optical Character Recognition");
        public static final OtfUnicodeRange ENCLOSED_ALPHANUMERICS = new OtfUnicodeRange(42, "Enclosed Alphanumerics");
        public static final OtfUnicodeRange BOX_DRAWING = new OtfUnicodeRange(43, "Box Drawing");
        public static final OtfUnicodeRange BLOCK_ELEMENTS = new OtfUnicodeRange(44, "Block Elements");
        public static final OtfUnicodeRange GEOMETRIC_SHAPES = new OtfUnicodeRange(45, "Geometric Shapes");
        public static final OtfUnicodeRange MISCELLANEOUS_SYMBOLS = new OtfUnicodeRange(46, "Miscellaneous Symbols");
        public static final OtfUnicodeRange DINGBATS = new OtfUnicodeRange(47, "Dingbats");
        public static final OtfUnicodeRange CJK_SYMBOLS_AND_PUNCTUATION = new OtfUnicodeRange(48, "CJK Symbols And Punctuation");
        public static final OtfUnicodeRange HIRAGANA = new OtfUnicodeRange(49, "Hiragana");
        public static final OtfUnicodeRange KATAKANA = new OtfUnicodeRange(50, "Katakana");
        public static final OtfUnicodeRange BOPOMOFO = new OtfUnicodeRange(51, "Bopomofo");
        public static final OtfUnicodeRange HANGUL_COMPATIBILITY_JAMO = new OtfUnicodeRange(52, "Hangul Compatibility Jamo");
        public static final OtfUnicodeRange PHAGS_PA = new OtfUnicodeRange(53, "Phags-pa");
        public static final OtfUnicodeRange ENCLOSED_CJK_LETTERS_AND_MONTHS = new OtfUnicodeRange(54, "Enclosed CJK Letters And Months");
        public static final OtfUnicodeRange CJK_COMPATIBILITY = new OtfUnicodeRange(55, "CJK Compatibility");
        public static final OtfUnicodeRange HANGUL_SYLLABLES = new OtfUnicodeRange(56, "Hangul Syllables");
        public static final OtfUnicodeRange NON_PLANE_0_ = new OtfUnicodeRange(57, "Non-Plane 0 *");
        public static final OtfUnicodeRange PHOENICIAN = new OtfUnicodeRange(58, "Phoenician");
        public static final OtfUnicodeRange CJK_UNIFIED_IDEOGRAPHS = new OtfUnicodeRange(59, "CJK Unified Ideographs");
        public static final OtfUnicodeRange PRIVATE_USE_AREA_PLANE_0 = new OtfUnicodeRange(60, "Private Use Area (plane 0)");
        public static final OtfUnicodeRange CJK_STROKES = new OtfUnicodeRange(61, "CJK Strokes");
        public static final OtfUnicodeRange ALPHABETIC_PRESENTATION_FORMS = new OtfUnicodeRange(62, "Alphabetic Presentation Forms");
        public static final OtfUnicodeRange ARABIC_PRESENTATION_FORMS_A = new OtfUnicodeRange(63, "Arabic Presentation Forms-A");
        public static final OtfUnicodeRange COMBINING_HALF_MARKS = new OtfUnicodeRange(64, "Combining Half Marks");
        public static final OtfUnicodeRange VERTICAL_FORMS = new OtfUnicodeRange(65, "Vertical Forms");
        public static final OtfUnicodeRange SMALL_FORM_VARIANTS = new OtfUnicodeRange(66, "Small Form Variants");
        public static final OtfUnicodeRange ARABIC_PRESENTATION_FORMS_B = new OtfUnicodeRange(67, "Arabic Presentation Forms-B");
        public static final OtfUnicodeRange HALFWIDTH_AND_FULLWIDTH_FORMS = new OtfUnicodeRange(68, "Halfwidth And Fullwidth Forms");
        public static final OtfUnicodeRange SPECIALS = new OtfUnicodeRange(69, "Specials");
        public static final OtfUnicodeRange TIBETAN = new OtfUnicodeRange(70, "Tibetan");
        public static final OtfUnicodeRange SYRIAC = new OtfUnicodeRange(71, "Syriac");
        public static final OtfUnicodeRange THAANA = new OtfUnicodeRange(72, "Thaana");
        public static final OtfUnicodeRange SINHALA = new OtfUnicodeRange(73, "Sinhala");
        public static final OtfUnicodeRange MYANMAR = new OtfUnicodeRange(74, "Myanmar");
        public static final OtfUnicodeRange ETHIOPIC = new OtfUnicodeRange(75, "Ethiopic");
        public static final OtfUnicodeRange CHEROKEE = new OtfUnicodeRange(76, "Cherokee");
        public static final OtfUnicodeRange UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS = new OtfUnicodeRange(77, "Unified Canadian Aboriginal Syllabics");
        public static final OtfUnicodeRange OGHAM = new OtfUnicodeRange(78, "Ogham");
        public static final OtfUnicodeRange RUNIC = new OtfUnicodeRange(79, "Runic");
        public static final OtfUnicodeRange KHMER = new OtfUnicodeRange(80, "Khmer");
        public static final OtfUnicodeRange MONGOLIAN = new OtfUnicodeRange(81, "Mongolian");
        public static final OtfUnicodeRange BRAILLE_PATTERNS = new OtfUnicodeRange(82, "Braille Patterns");
        public static final OtfUnicodeRange YI_SYLLABLES = new OtfUnicodeRange(83, "Yi Syllables");
        public static final OtfUnicodeRange TAGALOG = new OtfUnicodeRange(84, "Tagalog");
        public static final OtfUnicodeRange OLD_ITALIC = new OtfUnicodeRange(85, "Old Italic");
        public static final OtfUnicodeRange GOTHIC = new OtfUnicodeRange(86, "Gothic");
        public static final OtfUnicodeRange DESERET = new OtfUnicodeRange(87, "Deseret");
        public static final OtfUnicodeRange BYZANTINE_MUSICAL_SYMBOLS = new OtfUnicodeRange(88, "Byzantine Musical Symbols");
        public static final OtfUnicodeRange MATHEMATICAL_ALPHANUMERIC_SYMBOLS = new OtfUnicodeRange(89, "Mathematical Alphanumeric Symbols");
        public static final OtfUnicodeRange PRIVATE_USE_PLANE_15 = new OtfUnicodeRange(90, "Private Use (plane 15)");
        public static final OtfUnicodeRange VARIATION_SELECTORS = new OtfUnicodeRange(91, "Variation Selectors");
        public static final OtfUnicodeRange TAGS = new OtfUnicodeRange(92, "Tags");
        public static final OtfUnicodeRange LIMBU = new OtfUnicodeRange(93, "Limbu");
        public static final OtfUnicodeRange TAI_LE = new OtfUnicodeRange(94, "Tai Le");
        public static final OtfUnicodeRange NEW_TAI_LUE = new OtfUnicodeRange(95, "New Tai Lue");
        public static final OtfUnicodeRange BUGINESE = new OtfUnicodeRange(96, "Buginese");
        public static final OtfUnicodeRange GLAGOLITIC = new OtfUnicodeRange(97, "Glagolitic");
        public static final OtfUnicodeRange TIFINAGH = new OtfUnicodeRange(98, "Tifinagh");
        public static final OtfUnicodeRange YIJING_HEXAGRAM_SYMBOLS = new OtfUnicodeRange(99, "Yijing Hexagram Symbols");
        public static final OtfUnicodeRange SYLOTI_NAGRI = new OtfUnicodeRange(100, "Syloti Nagri");
        public static final OtfUnicodeRange LINEAR_B_SYLLABARY = new OtfUnicodeRange(101, "Linear B Syllabary");
        public static final OtfUnicodeRange ANCIENT_GREEK_NUMBERS = new OtfUnicodeRange(102, "Ancient Greek Numbers");
        public static final OtfUnicodeRange UGARITIC = new OtfUnicodeRange(103, "Ugaritic");
        public static final OtfUnicodeRange OLD_PERSIAN = new OtfUnicodeRange(104, "Old Persian");
        public static final OtfUnicodeRange SHAVIAN = new OtfUnicodeRange(105, "Shavian");
        public static final OtfUnicodeRange OSMANYA = new OtfUnicodeRange(106, "Osmanya");
        public static final OtfUnicodeRange CYPRIOT_SYLLABARY = new OtfUnicodeRange(107, "Cypriot Syllabary");
        public static final OtfUnicodeRange KHAROSHTHI = new OtfUnicodeRange(108, "Kharoshthi");
        public static final OtfUnicodeRange TAI_XUAN_JING_SYMBOLS = new OtfUnicodeRange(109, "Tai Xuan Jing Symbols");
        public static final OtfUnicodeRange CUNEIFORM = new OtfUnicodeRange(110, "Cuneiform");
        public static final OtfUnicodeRange COUNTING_ROD_NUMERALS = new OtfUnicodeRange(111, "Counting Rod Numerals");
        public static final OtfUnicodeRange SUNDANESE = new OtfUnicodeRange(112, "Sundanese");
        public static final OtfUnicodeRange LEPCHA = new OtfUnicodeRange(113, "Lepcha");
        public static final OtfUnicodeRange OL_CHIKI = new OtfUnicodeRange(114, "Ol Chiki");
        public static final OtfUnicodeRange SAURASHTRA = new OtfUnicodeRange(115, "Saurashtra");
        public static final OtfUnicodeRange KAYAH_LI = new OtfUnicodeRange(116, "Kayah Li");
        public static final OtfUnicodeRange REJANG = new OtfUnicodeRange(117, "Rejang");
        public static final OtfUnicodeRange CHAM = new OtfUnicodeRange(118, "Cham");
        public static final OtfUnicodeRange ANCIENT_SYMBOLS = new OtfUnicodeRange(119, "Ancient Symbols");
        public static final OtfUnicodeRange PHAISTOS_DISC = new OtfUnicodeRange(120, "Phaistos Disc");
        public static final OtfUnicodeRange CARIAN = new OtfUnicodeRange(121, "Carian");
        public static final OtfUnicodeRange DOMINO_TILES = new OtfUnicodeRange(122, "Domino Tiles");
    }
}
