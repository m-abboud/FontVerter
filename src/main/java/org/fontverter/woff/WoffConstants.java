package org.fontverter.woff;

public class WoffConstants {
    public enum TableFlagType {
        cmap(0), EBLC(16), CBDT(32), gvar(48),
        head(1), gasp(17), CBLC(33), hsty(49),
        hhea(2), hdmx(18), COLR(34), just(50),
        hmtx(3), kern(19), CPAL(35), lcar(51),
        maxp(4), LTSH(20), SVG(36), mort(52),
        name(5), PCLT(21), sbix(37), morx(53),
        OS2(6), VDMX(22), acnt(38), opbd(54),
        post(7), vhea(23), avar(39), prop(55),
        cvt(8), vmtx(24), bdat(40), trak(56),
        fpgm(9), BASE(25), bloc(41), ZapF(57),
        glyf(10), GDEF(26), bsln(42), SilF(58),
        loca(11), GPOS(27), cvar(43), GlaT(59),
        prep(12), GSUB(28), fdsc(44), GloC(60),
        CFF(13), EBSC(29), feat(45), FeaT(61),
        VORG(14), JSTF(30), fmtx(46), SilL(62),
        EBDT(15), MATH(31), fvar(47), arbitrary(63);

        private final int value;

        TableFlagType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static TableFlagType fromInt(int i) {
            for (TableFlagType typeOn : TableFlagType.values())
                if (typeOn.getValue() == i) {
                    return typeOn;
                }
            return null;
        }

        public String toString() {
            return this.name();
        }
    }


}
