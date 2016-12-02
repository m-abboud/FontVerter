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

package org.mabb.fontverter.opentype.TtfInstructions;

import org.mabb.fontverter.opentype.ControlValueTable;
import org.mabb.fontverter.opentype.OpenTypeFont;
import org.mabb.fontverter.opentype.TtfInstructions.graphicsengine.RoundSettings;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static org.mabb.fontverter.opentype.TtfInstructions.TtfGraphicsState.ScanConverterMode.DROPOUT_WITH_STUBS;

public class TtfGraphicsState {
    public RoundSettings.RoundState roundState = RoundSettings.RoundState.ROUND;
    public RoundSettings roundSettings = new RoundSettings();
    public Long angleWeight = 0L;

    public ScanConverterMode scanConverterMode = DROPOUT_WITH_STUBS;
    public List<ScanDropoutMode> dropoutControlModes = new ArrayList<ScanDropoutMode>();
    public long droputThreshold = 1L;

    public List<Short> cvtValues;
    public Float cvtCutInValue = 0F;
    public Long deltaBase = 9L;
    public Long deltaShift = 3L;
    public Float minimumDistance = 1f;
    public long singleWidth = 0;
    public long singleWidthCutIn = 0;

    public Point2D.Double freedomVector = new Point2D.Double(1, 1);
    public Point2D.Double projectionVector = new Point2D.Double(1, 1);

    public Long referencePoint0Id = 0L;
    public Long referencePoint1Id = 0L;
    public Long referencePoint2Id = 0L;

    public Long zone0Id = 0L;
    public Long zone1Id = 0L;
    public Long zone2Id = 0L;


    public void initialize(OpenTypeFont font) {
        // since cvt values are changed by instructions and we don't want to write to the actual font
        // we make a copy before execution
        // font == null from lazy unit test code
        if (font != null && font.getCvt() != null)
            cvtValues = new ArrayList<Short>(font.getCvt().getValues());
    }


    public Short getCvtValue(Long index) throws ControlValueTable.CvtValueNotFoundException {
        if (index > cvtValues.size())
            throw new ControlValueTable.CvtValueNotFoundException();

        return cvtValues.get(index.intValue());
    }

    public void setCvtValue(Long index, Long number) throws ControlValueTable.CvtValueNotFoundException {
        if (index > cvtValues.size())
            throw new ControlValueTable.CvtValueNotFoundException();

        cvtValues.set(index.intValue(), number.shortValue());
    }

    public Float round(Float number) {
        // todo actually use round settings and state
        return (float) Math.round(number);
    }

    public enum ScanDropoutMode {
        TRUE_IF_PPEM_LESS_THAN_THRESHOLD,
        TRUE_IF_GLYPH_IS_ROTATED,
        TRUE_IF_GLYPH_STRETCHED,
        FALSE_UNLESS_PPEM_LESS_THAN_THRESHOLD,
        FALSE_UNLESS_ROTATED,
        FALSE_UNLESS_STRETCHED,
    }

    public enum ScanConverterMode {
        DROPOUT_WITH_STUBS(0),
        DROPOUT_EXCLUDING_STUBS(1),
        NON_DROPOUT(2);

        public final int id;

        ScanConverterMode(int id) {
            this.id = id;
        }

        public short getId() {
            return (short) id;
        }

        public static ScanConverterMode fromValue(int code) {
            for (ScanConverterMode val : ScanConverterMode.values())
                if (val.id == code)
                    return val;
            return DROPOUT_WITH_STUBS;
        }
    }
}
