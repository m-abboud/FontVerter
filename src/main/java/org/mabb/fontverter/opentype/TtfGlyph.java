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

import org.apache.commons.lang3.StringUtils;
import org.mabb.fontverter.FontVerterUtils;
import org.mabb.fontverter.GlyphMapReader;
import org.mabb.fontverter.io.*;
import org.mabb.fontverter.opentype.TtfInstructions.TtfInstructionParser;
import org.slf4j.Logger;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.mabb.fontverter.io.DataTypeProperty.DataType.*;
import static org.mabb.fontverter.opentype.TtfGlyph.CoordinateFlagType.*;
import static org.mabb.fontverter.opentype.TtfInstructions.TtfInstructionParser.*;
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

    private List<GlyphCoordinate> points = new LinkedList<GlyphCoordinate>();

    private OpenTypeFont font;
    private int glyphId;
    byte[] rawData;
    private boolean isParsed;
    private boolean useRawData = false;

    public static TtfGlyph parse(byte[] data, OpenTypeFont font) throws IOException {
        FontDataInputStream reader = new FontDataInputStream(data);

        DataTypeBindingDeserializer deserializer = new DataTypeBindingDeserializer();
        TtfGlyph glyph = (TtfGlyph) deserializer.deserialize(reader, TtfGlyph.class);
        glyph.isParsed = true;

        glyph.font = font;
        glyph.glyphId = font.getGlyfTable().getGlyphs().size();
        glyph.rawData = data;

        if (glyph.isComposite())
            glyph.useRawData = true;
        else
            // x[] and y[] vals can be variable length so have to do manually vs annotation
            glyph.readSimpleGlyphData(reader);

        return glyph;
    }

    public TtfGlyph() {
        isParsed = false;
    }

    public byte[] generateData() throws IOException {
        if (isEmpty())
            return new byte[0];

        // dont support write for composite glyphs yet
        if (rawData != null && useRawData)
            return rawData;


        int t = 85;
        if (instructionLength > t)
        instructionLength = t;

        FontDataOutputStream writer = new FontDataOutputStream();
        DataTypeBindingSerializer serializer = new DataTypeBindingSerializer();
        byte[] data = serializer.serialize(this);
        writer.write(data);

        for (GlyphCoordinate pointOn : points) {
            CoordinateFlagSet coordFlags = new CoordinateFlagSet();
            if (pointOn.isOnCurve())
                coordFlags.add(ON_CURVE);

            writer.write(coordFlags.write());
        }

        for (GlyphCoordinate pointOn : points)
            writer.writeShort((int) pointOn.x);

        for (GlyphCoordinate pointOn : points)
            writer.writeShort((int) pointOn.y);

        if (writer.size() % 2 != 0)
            writer.writeByte(0);

        return writer.toByteArray();
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
                i += flagSet.repeatCount;
            }

            this.flags.add(flagSet);
        }
    }

    private void readCoordinates(FontDataInputStream reader) throws IOException {
        for (CoordinateFlagSet flagSetOn : flags) {
            List<Integer> coords = tryGetXCoords(reader, flagSetOn);
            if (coords == null)
                break;

            for (Integer xOn : coords)
                points.add(new GlyphCoordinate(xOn, 0, flagSetOn));
        }

        int yIndex = 0;
        for (CoordinateFlagSet flagSetOn : flags) {
            try {
                List<Integer> coords = tryGetYCoords(reader, flagSetOn, yIndex);
                if (coords == null)
                    break;

                for (int i = 0; i < coords.size(); i++)
                    points.get(i + yIndex).y = coords.get(i);

                yIndex += coords.size();
            } catch (EOFException ex) {
                log.warn("Went over on y coord read at flag #" + flags.indexOf(flagSetOn));
                useRawData = true;
                break;
            }
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
                coords.add(-(reader.readByte() & 0xFF));
            else if (is1Byte)
                coords.add(reader.readByte() & 0xFF);

            else if (useSame2ByteCoord)
                coords.add(0);
            else
                coords.add((int) reader.readShort());
        }

        if (coords.size() == 0)
            throw new IOException("TTF glyph read coordinates array of 0 " + (isX ? "Xs" : "Ys"));

        return coords;
    }

    private List<Integer> tryGetXCoords(FontDataInputStream reader, CoordinateFlagSet flagSetOn)
            throws IOException {
        try {
            int lastCoord = points.size() == 0 ? 0 : (int) points.get(points.size() - 1).x;
            return readCoordinatesForFlag(reader, flagSetOn, lastCoord);
        } catch (EOFException ex) {
            log.warn("EOF on X coord read at flag #" + flags.indexOf(flagSetOn) + " " + this.toString());
            useRawData = true;
            return null;
        }
    }

    private List<Integer> tryGetYCoords(FontDataInputStream reader, CoordinateFlagSet flagSetOn, int yIndex)
            throws IOException {
        try {
            CoordinateFlagSet coordFlags = new CoordinateFlagSet(flagSetOn);
            coordFlags.forX = false;
            int lastCoord = yIndex == 0 ? 0 : (int) points.get(yIndex - 1).y;

            return readCoordinatesForFlag(reader, coordFlags, lastCoord);
        } catch (EOFException ex) {
            log.warn("Went over on y coord read at flag #" + flags.indexOf(flagSetOn));
            useRawData = true;
            return null;
        }
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

    public List<GlyphCoordinate> getCoordinates() {
        return points;
    }

    /**
     * Debug only method at the moment, will not return an entireley accurate path in many cases.
     */
    List<Path2D.Double> getPaths() {
        LinkedList<Path2D.Double> paths = new LinkedList<Path2D.Double>();

        int startPtOn = 0;
        Point2D.Double lastPoint = new Point2D.Double();

        for (Integer endPtOn : countourEndPoints) {
            Path2D.Double pathOn = new Path2D.Double();

            if (startPtOn == 0)
                pathOn.moveTo(0, 0);

            Point2D.Double firstPoint = new Point2D.Double();

            for (int i = startPtOn; i < endPtOn + 1; i++) {

                Point2D.Double relativePoint = points.get(i);
                Point2D.Double point = new Point2D.Double();
                point.x = relativePoint.x + lastPoint.x;
                point.y = relativePoint.y + lastPoint.y;

                if (startPtOn != 0 && i == startPtOn)
                    pathOn.moveTo(point.x, point.y);
                else
                    pathOn.lineTo(point.x, point.y);

                if (i == startPtOn)
                    firstPoint = point;

                lastPoint = point;
            }
            startPtOn = endPtOn + 1;

            pathOn.lineTo(firstPoint.x, firstPoint.y);
            paths.add(pathOn);
        }

        return paths;
    }

    List<Countour> getCountours() {
        List<Countour> countours = new LinkedList<Countour>();

        int startPtOn = 0;
        Point2D.Double lastPoint = new Point2D.Double();

        for (Integer endPtOn : countourEndPoints) {
            Countour countourOn = new Countour();

            Point2D.Double firstPoint = new Point2D.Double();

            for (int i = startPtOn; i < endPtOn + 1; i++) {

                Point2D.Double relativePoint = points.get(i);
                Point2D.Double point = new Point2D.Double();
                point.x = relativePoint.x + lastPoint.x;
                point.y = relativePoint.y + lastPoint.y;

                countourOn.add(relativePoint);
//                countourOn.add(point);

                if (i == startPtOn)
                    firstPoint = point;

                lastPoint = point;
            }
            startPtOn = endPtOn + 1;

            countourOn.add(firstPoint);
            countours.add(countourOn);
        }

        return countours;
    }

    public void setCountours(List<Countour> countours) {
        points.clear();
        for (Countour countourOn : countours) {
            for (int i = 0; i < countourOn.size(); i++) {
                Point2D.Double ptOn = countourOn.get(i);

                // last point is filler for otf glyph
                if (i != countourOn.size() - 1)
                    points.add(new GlyphCoordinate(ptOn.x, ptOn.y, new CoordinateFlagSet()));
            }
        }
    }

    public String toString() {
        if (font.getCmap() == null)
            return "CMap table is null can not get string data for glyph.";

        String names = "";
        for (GlyphMapReader.GlyphMapping mapOn : font.getCmap().getGlyphMappings())
            if (mapOn.glyphId == glyphId)
                names += mapOn.name + ", ";

        return String.format("Glyph Index:'%d' Used For Chars:'%s'", glyphId, names);
    }

    public boolean isEmpty() {
        return !isParsed && points.size() == 0 && rawData == null;
    }

    public List<TtfInstructionParser.TtfInstruction> getInstructions()
            throws IllegalAccessException, IOException, InstantiationException {
        try {
            TtfInstructionParser parser = new TtfInstructionParser();

            return parser.parse(FontVerterUtils.toPrimative(instructions));
        } catch (Exception ex) {
            log.info("Failed to parse ttfinstrctuins, currentley uneeded for conversion");

            return new ArrayList<TtfInstructionParser.TtfInstruction>();
        }
    }

    protected static class GlyphCoordinate extends Point2D.Double {
        CoordinateFlagSet flags;

        public GlyphCoordinate(double x, double y, CoordinateFlagSet flags) {
            super(x, y);
            this.flags = flags;
        }

        public boolean isOnCurve() {
            return flags.contains(ON_CURVE);
        }
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

        public byte write() {
            char[] binary = StringUtils.repeat("0", 8).toCharArray();

            for (CoordinateFlagType typeOn : this) {
                int bit = 8 - typeOn.getValue() - 1;
                binary[bit] = '1';
            }

            return (byte) Integer.parseInt(String.valueOf(binary));
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
        public static final CoordinateFlagType POSITIVE_X_SHORT = THIS_X_IS_SAME;
        public static final CoordinateFlagType POSITIVE_Y_SHORT = THIS_Y_IS_SAME;

        private final int value;

        CoordinateFlagType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
