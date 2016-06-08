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

import org.apache.commons.lang3.StringUtils;
import org.mabb.fontverter.io.DataTypeBindingDeserializer;
import org.mabb.fontverter.io.DataTypeProperty;
import org.mabb.fontverter.io.FontDataInputStream;
import org.slf4j.Logger;

import java.awt.geom.Point2D;
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.mabb.fontverter.io.DataTypeProperty.DataType.*;
import static org.mabb.fontverter.opentype.TtfGlyph.CoordinateFlagType.*;
import static org.slf4j.LoggerFactory.getLogger;

public class TtfGlyph {
    private static final Logger log = getLogger(TtfGlyph.class);

    @DataTypeProperty(dataType = SHORT)
    short numberOfContours;

    @DataTypeProperty(dataType = SHORT)
    short xMin;

    @DataTypeProperty(dataType = SHORT)
    short yMin;

    @DataTypeProperty(dataType = SHORT)
    short xMax;

    @DataTypeProperty(dataType = SHORT)
    short yMax;

    @DataTypeProperty(dataType = USHORT, isArray = true, arrayLength = "getNumberOfContours", ignoreIf = "isComposite")
    Integer[] countourEndPoints;

    @DataTypeProperty(dataType = USHORT, ignoreIf = "isComposite")
    int instructionLength;

    @DataTypeProperty(dataType = BYTE, isArray = true, ignoreIf = "isComposite", arrayLength = "instructionLength")
    Byte[] instructions;

    List<CoordinateFlagSet> flags = new LinkedList<CoordinateFlagSet>();

    private List<Point2D.Double> points = new LinkedList<Point2D.Double>();

    public static TtfGlyph parse(FontDataInputStream reader) throws IOException {
        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        TtfGlyph glyph = (TtfGlyph) deserializer.deserialize(reader, TtfGlyph.class);

        // x[] and y[] vals can be variable length so have to do manually
        if (!glyph.isComposite())
            glyph.readSimpleGlyphData(reader);

        return glyph;
    }

    private void readSimpleGlyphData(FontDataInputStream reader) throws IOException {
        readFlags(reader);
        readCoordinates(reader);
    }

    private void readFlags(FontDataInputStream reader) throws IOException {
        for (int i = 0; i < getNumberOfPoints(); i++) {
            CoordinateFlagSet flagSet = CoordinateFlagSet.flagsFromByte(reader.readByte());

            if (flagSet.contains(REPEAT)) {
                flagSet.repeatCount = reader.readByte() & 0xFF;
                i++;
            }

            this.flags.add(flagSet);
        }
    }

    private void readCoordinates(FontDataInputStream reader) throws IOException {
        List<Integer> xCoords = new LinkedList<Integer>();
        List<Integer> yCoords = new LinkedList<Integer>();

        for (CoordinateFlagSet flagSetOn : flags) {
            int lastCoord = xCoords.size() == 0 ? 0 : xCoords.get(xCoords.size() - 1);
            List<Integer> coords = readCoordinatesForFlag(reader, flagSetOn, lastCoord);

            xCoords.addAll(coords);
        }


        for (CoordinateFlagSet flagSetOn : flags) {
            try {
                CoordinateFlagSet coordFlags = new CoordinateFlagSet(flagSetOn);
                coordFlags.forX = false;
                int lastCoord = yCoords.size() == 0 ? 0 : yCoords.get(yCoords.size() - 1);

                List<Integer> coords = readCoordinatesForFlag(reader, coordFlags, lastCoord);

                yCoords.addAll(coords);
            } catch (EOFException ex) {
                log.error("Went over on y coord read at flag #" + flags.indexOf(flagSetOn));
                throw (ex);
            }
        }

        for (int pointIndex = 0; pointIndex < xCoords.size(); pointIndex++) {
            Integer x = xCoords.get(pointIndex);
            Integer y = yCoords.get(pointIndex);

            points.add(new Point2D.Double(x, y));
        }
    }

    private List<Integer> readCoordinatesForFlag(FontDataInputStream reader, CoordinateFlagSet coordFlags, int lastCoord)
            throws IOException {
        boolean isX = coordFlags.forX;
        boolean is1Byte = coordFlags.contains(isX ? IS_X_1_BYTE : IS_Y_1_BYTE);
        boolean useSame2ByteCoord = coordFlags.contains(isX ? THIS_X_IS_SAME : THIS_Y_IS_SAME);
        boolean useNegative1Byte = !useSame2ByteCoord;

        List<Integer> coords = new LinkedList<Integer>();

        for (int j = 0; j < coordFlags.repeatCount + 1; j++) {
            if (useNegative1Byte && is1Byte)
                coords.add(-((int) reader.readByte()));
            else if (is1Byte)
                coords.add((int) reader.readByte());

            else if (useSame2ByteCoord)
                coords.add(lastCoord);
            else
                coords.add((int) reader.readShort());
        }

        if (coords.size() == 0)
            throw new IOException("TTF glyph read coordinates array of 0 " + (isX ? "Xs" : "Ys"));

        return coords;
    }

    public short getNumberOfContours() {
        return numberOfContours;
    }

    public boolean isComposite() {
        return numberOfContours < 0;
    }

    int getNumberOfPoints() {
        if (countourEndPoints == null)
            return 0;

        return countourEndPoints[countourEndPoints.length - 1] + 1;
    }

    public List<Point2D.Double> getCoordinates() {
        return points;
    }

    protected static class CoordinateFlagSet extends LinkedList<CoordinateFlagType> {
        public int repeatCount = 0;
        boolean forX = true;

        public CoordinateFlagSet() {
            super();
        }

        public CoordinateFlagSet(CoordinateFlagSet coordinateFlagTypes) {
            super(coordinateFlagTypes);

            this.repeatCount = coordinateFlagTypes.repeatCount;
            this.forX = coordinateFlagTypes.forX;
        }

        public static CoordinateFlagSet flagsFromByte(byte flagByte) {
            CoordinateFlagSet flags = new CoordinateFlagSet();

            String binary = Integer.toBinaryString(flagByte);
            binary = StringUtils.repeat("0", 8 - binary.length()) + binary;

            for (CoordinateFlagType typeOn : CoordinateFlagType.values()) {
                char bitOn = binary.charAt(8 - typeOn.getValue() - 1);

                if (bitOn == '1')
                    flags.add(typeOn);
            }

            return flags;
        }
    }

    protected enum CoordinateFlagType {
        ON_CURVE(0),
        IS_X_1_BYTE(1),
        IS_Y_1_BYTE(2),
        REPEAT(3),
        THIS_X_IS_SAME(4),
        THIS_Y_IS_SAME(5);

        private final int value;

        CoordinateFlagType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
