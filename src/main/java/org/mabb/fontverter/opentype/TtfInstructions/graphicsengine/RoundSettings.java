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

package org.mabb.fontverter.opentype.TtfInstructions.graphicsengine;

public class RoundSettings {
    public double period = 0;
    public double phase = 0;
    public double threshold = 0;

    public void updateForFlags(Long flags) {
        byte periodFlag = (byte) ((flags >> 6) & 3);
        setPeriodFromFlag(periodFlag);

        byte phaseFlag = (byte) ((flags >> 4) & 3);
        setPhaseFromFlag(phaseFlag);

        byte thresholdFlag = (byte) (flags & 0xF);
        setThresholdFromFlag(thresholdFlag);
    }

    public void updateFor45DegreeFlags(Long flags) {
        set45DegPeriodFromFlag((byte) ((flags >> 6) & 3));

        setPhaseFromFlag((byte) ((flags >> 4) & 3));
        setThresholdFromFlag((byte) (flags & 0xF));
    }

    private void setPeriodFromFlag(byte periodFlag) {
        if (periodFlag == 0)
            period = 1.0 / 2.0;
        else if (periodFlag == 1)
            period = 1;
        else if (periodFlag == 2)
            period = 2;
    }

    private void set45DegPeriodFromFlag(byte periodFlag) {
        if (periodFlag == 0)
            period = Math.sqrt(2) / 2.0;
        else if (periodFlag == 1)
            period = Math.sqrt(2);
        else if (periodFlag == 2)
            period = 2 * Math.sqrt(2);
    }

    private void setPhaseFromFlag(byte phaseFlag) {
        if (phaseFlag == 0)
            phase = 0;
        else if (phaseFlag == 1)
            phase = period / 4.0;
        else if (phaseFlag == 2)
            phase = period / 2;
        else if (phaseFlag == 3)
            phase = period * (3/4);
    }

    private void setThresholdFromFlag(byte thresholdFlag) {
        if (thresholdFlag == 0)
            threshold = period - 1;
        else {
            int periodDividend = -3;
            periodDividend += (thresholdFlag - 1);
            threshold = (periodDividend / 8.0) * period;
        }
    }

    public static enum RoundState {
        OFF,
        ROUND,
        ROUND_TO_GRID,
        ROUND_DOWN_TO_GRID,
        ROUND_UP_TO_GRID,
        ROUND_TO_DOUBLE_GRID,
        ROUND_TO_HALF_GRID,
        SUPER_ROUND,
        SUPER_ROUND_45_DEG,
    }
}
