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

import org.apache.fontbox.cff.CFFStandardEncoding;
import org.mabb.fontverter.GlyphMapReader;
import org.mabb.fontverter.io.FontDataInputStream;
import org.mabb.fontverter.io.FontDataOutputStream;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

class Format4SubTable extends CmapSubTable {
    private static final Logger log = getLogger(Format4SubTable.class);

    private static final int FORMAT4_HEADER_SIZE = 16;
    // LinkedHashMap important, for keeping ordering the same for loops
    private Map<Integer, Integer> charCodeToGlyphId = new LinkedHashMap<Integer, Integer>();
    List<Integer> deltas;
    List<Integer> ends;
    List<Integer> starts;
    List<Integer> idRangeOffsets;
    List<List<IndexedGlyph>> idRangeGlyphs;

    private int glyphsStartPos;

    public Format4SubTable() {
        formatNumber = 4;
    }

    public byte[] getData() throws IOException {
        FontDataOutputStream writer = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        // kludge for read otf fonts
        if (rawReadData != null) {
            writer.writeUnsignedShort(formatNumber);
            writer.writeUnsignedShort(rawReadData.length + 4);
            writer.write(rawReadData);

            return writer.toByteArray();
        }

        calculateSegments();

        writer.writeUnsignedShort((int) formatNumber);
        writer.writeUnsignedShort(getLength());
        writer.writeUnsignedShort((int) getLanguageId());

        writer.writeUnsignedShort(getSegmentCount() * 2);
        writer.writeUnsignedShort(getSearchRange());
        writer.writeUnsignedShort(getEntrySelector());
        writer.writeUnsignedShort(getRangeShift());

        for (Integer endEntryOn : ends)
            writer.writeUnsignedShort(endEntryOn);
        // array end code
        writer.writeUnsignedShort(65535);

        // 'reservedPad' Set to 0
        writer.writeUnsignedShort(0);

        for (Integer startEntryOn : starts)
            writer.writeUnsignedShort(startEntryOn);
        // array end code
        writer.writeUnsignedShort(65535);

        for (Integer deltaEntryOn : deltas)
            writer.writeUnsignedShort(deltaEntryOn);
        // array end code
        writer.writeUnsignedShort(1);

        for (Integer rangeOffsets : idRangeOffsets)
            writer.writeUnsignedShort(0);
//            writer.writeUnsignedShort(rangeOffsets);
        // array end code
        writer.writeUnsignedShort(0);

//        writeIndexedGlyphs(writer);

        byte[] data = writer.toByteArray();
        setDataHeaderLength(data);
        return data;
    }

    private void writeIndexedGlyphs(FontDataOutputStream writer) throws IOException {
        glyphsStartPos = writer.currentPosition();

        for (int segIndex = 0; segIndex < idRangeOffsets.size(); segIndex++) {
            Integer idRangeOn = idRangeOffsets.get(segIndex);
            if (idRangeOn == 0)
                continue;

            writeSegmentGlyphs(writer, segIndex);
        }
    }

    private void writeSegmentGlyphs(FontDataOutputStream writer, int segIndex) throws IOException {
        Integer idRangeOn = idRangeOffsets.get(segIndex);

        List<IndexedGlyph> glyphs = idRangeGlyphs.get(segIndex);
        for (int glyphIndex = 0; glyphIndex < glyphs.size(); glyphIndex++) {
            int position = writer.currentPosition();

            long glyphOffset = glyphsStartPos + ((idRangeOn / 2) + glyphIndex + (segIndex - getSegmentCount())) * 2;

            int paddingNeeded = (int) (glyphOffset - position - 1);
            if (paddingNeeded > 0)
                writer.write(new byte[paddingNeeded]);

            IndexedGlyph glyphOn = glyphs.get(glyphIndex);
            writer.writeUnsignedShort(glyphOn.glyphId);

            log.warn(String.format("Wrote cmapsub4 indexed glyph. glyphId:%d glyphOffset:%d idRangeOffset: %d charCode:%d",
                    glyphOn.glyphId, glyphOffset, idRangeOn, glyphOn.charCode));
        }
    }

    private void calculateSegments() {
        initSegments();
        List<Map.Entry<Integer, Integer>> entries = getOrderedCharCodeToGlyphIds();

        // cmaps can be big so keep track of count of range offsets that are set and all of
        // segment calcs in one big ugly loop for same reason and ugly 5 param sub methods
        // that would have to be created elsewise.
        int numSetOffsets = 0;
        int lastCharCode = -1;
        int lastGlyphId = -1;

        for (Map.Entry<Integer, Integer> entryOn : entries) {
            int charCodeOn = entryOn.getKey();
            int glyphIdOn = entryOn.getValue();
            int delta = glyphIdOn - charCodeOn;

            boolean needAddSegment = charCodeOn != lastCharCode + 1 || lastGlyphId + 1 != glyphIdOn;

            if (needAddSegment) {
                starts.add(charCodeOn);
                deltas.add(delta);

                if (delta < 0) {
                    idRangeOffsets.add((numSetOffsets + 1) * 2);
                    numSetOffsets++;
                } else
                    idRangeOffsets.add(0);

                idRangeGlyphs.add(new LinkedList<IndexedGlyph>());
            }

            int lastDeltaOn = deltas.get(deltas.size() - 1);
            if (lastDeltaOn < 0) {
                List<IndexedGlyph> rangeGlyphs = idRangeGlyphs.get(idRangeGlyphs.size() - 1);
                rangeGlyphs.add(new IndexedGlyph(glyphIdOn, charCodeOn));
            }

            if (needAddSegment && lastCharCode != -1)
                ends.add(lastCharCode);

            lastCharCode = charCodeOn;
        }

        // add last one not caught in loop
        if (entries.size() >= 1)
            ends.add(entries.get(entries.size() - 1).getKey());
    }

    private void initSegments() {
        deltas = new LinkedList<Integer>();
        starts = new LinkedList<Integer>();
        ends = new LinkedList<Integer>();
        idRangeOffsets = new LinkedList<Integer>();
        idRangeGlyphs = new LinkedList<List<IndexedGlyph>>();
    }

    public void readData(FontDataInputStream input) throws IOException {
        int length = input.readUnsignedShort();
        rawReadData = input.readBytes(length - 4);
        input = new FontDataInputStream(rawReadData);

        languageId = input.readUnsignedShort();
        int segmentCount = input.readUnsignedShort() / 2;
        int searchRange = input.readUnsignedShort();
        int entrySelector = input.readUnsignedShort();
        int rangeShift = input.readUnsignedShort();

        int[] charCodeRangeEnds = input.readUnsignedShortArray(segmentCount);
        int reservedPad = input.readUnsignedShort();

        int[] charCodeRangeStarts = input.readUnsignedShortArray(segmentCount);
        int[] idDelta = input.readUnsignedShortArray(segmentCount);
        int[] idRangeOffset = input.readUnsignedShortArray(segmentCount);
        long glyphIndexStartPos = input.getPosition();

        for (int segOn = 0; segOn < segmentCount - 1; segOn++) {
            int start = charCodeRangeStarts[segOn];
            int end = charCodeRangeEnds[segOn];
            int delta = idDelta[segOn];
            int offset = idRangeOffset[segOn];

            for (int charCode = start; charCode <= end; charCode++) {
                if (offset == 0) {
                    int glyphId = (delta + charCode) % 65536;
                    charCodeToGlyphId.put(charCode, glyphId);
                } else {
                    long glyphOffset = ((offset / 2) + (charCode - start) + (segOn - segmentCount)) * 2;
                    glyphOffset += glyphIndexStartPos;

                    input.seek((int) glyphOffset);
                    int glyphArrIndex = input.readUnsignedShort();

                    if (glyphArrIndex != 0) {
                        glyphArrIndex = (glyphArrIndex + delta) % 65536;
                        charCodeToGlyphId.put(charCode, glyphArrIndex);
                    }
                }
            }
        }
    }

    private void setDataHeaderLength(byte[] data) throws IOException {
        FontDataOutputStream lengthWriter = new FontDataOutputStream(FontDataOutputStream.OPEN_TYPE_CHARSET);
        lengthWriter.writeUnsignedShort(data.length);
        byte[] lengthData = lengthWriter.toByteArray();
        data[2] = lengthData[0];
        data[3] = lengthData[1];
    }

    public int glyphCount() {
        return charCodeToGlyphId.size() + 1;
    }

    private int getSegmentCount() {
        // +1 for padding at end of segment arrays
        return ends.size() + 1;
    }

    private int getSearchRange() {
        double logFloor = Math.floor(log2(getSegmentCount()));
        return (int) (2 * (Math.pow(2, logFloor)));
    }

    private int getEntrySelector() {
        return (int) log2(getSearchRange() / 2);
    }

    private int getRangeShift() {
        return 2 * getSegmentCount() - getSearchRange();
    }

    private double log2(int number) {
        return Math.log(number) / Math.log(2);
    }

    private int getLength() {
        return FORMAT4_HEADER_SIZE + ((charCodeToGlyphId.size()) * 8);
    }

    public void addGlyphMapping(int characterCode, int glyphId) {
        charCodeToGlyphId.put(characterCode, glyphId);
    }

    public List<GlyphMapReader.GlyphMapping> getGlyphMappings() {
        return GlyphMapReader.readCharCodesToGlyphs(charCodeToGlyphId, CFFStandardEncoding.getInstance());
    }

    private List<Map.Entry<Integer, Integer>> getOrderedCharCodeToGlyphIds() {
        List<Map.Entry<Integer, Integer>> charCodeEntries = new ArrayList<Map.Entry<Integer, Integer>>();
        for (Map.Entry<Integer, Integer> entryOn : charCodeToGlyphId.entrySet())
            charCodeEntries.add(entryOn);

        Collections.sort(charCodeEntries, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o1.getKey() < o2.getKey() ? -1 : o1.getKey().equals(o2.getKey()) ? 0 : 1;
            }
        });

        return charCodeEntries;
    }

    private static class IndexedGlyph {
        public int glyphId;
        public int charCode;

        public IndexedGlyph(int glyphId, int charCode) {
            this.glyphId = glyphId;
            this.charCode = charCode;
        }
    }
}
