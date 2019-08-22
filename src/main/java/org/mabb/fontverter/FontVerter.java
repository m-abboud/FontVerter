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

package org.mabb.fontverter;

import org.apache.commons.io.FileUtils;
import org.mabb.fontverter.converter.FontConverter;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FontVerter {

	private static List<Class<? extends FVFont>> adapters;
    private static final Object adapterLock = new Object();

    public enum FontFormat {
        OTF,
        WOFF1,
        WOFF2,
        BARE_CFF,
        EOT;

        public static final FontFormat TTF = OTF;

        public static FontFormat fromString(String value) {
            if (value.equalsIgnoreCase("WOFF"))
                return WOFF1;
            if (value.equalsIgnoreCase("OpenType"))
                return OTF;
            if (value.equalsIgnoreCase("TrueType"))
                return OTF;
            if (value.equalsIgnoreCase("EOT"))
                return EOT;

            return FontFormat.valueOf(value.toUpperCase());
        }
    }

    public static FVFont convertFont(FVFont inputFont, FontFormat convertTo) throws IOException {
        FontConverter converter = inputFont.createConverterForType(convertTo);

        return converter.convertFont(inputFont);
    }

    public static FVFont convertFont(byte[] inputFontData, FontFormat convertTo) throws IOException {
        FVFont inputFont = readFont(inputFontData);
        return convertFont(inputFont, convertTo);
    }

    public static FVFont convertFont(File inputFontData, FontFormat convertTo) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(inputFontData);
        return convertFont(data, convertTo);
    }

    public static FVFont convertFont(String inputFontData, FontFormat convertTo) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File(inputFontData));
        return convertFont(data, convertTo);
    }

    public static FVFont readFont(File fontFile) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(fontFile);
        return readFont(data);
    }

    public static FVFont readFont(String fontFile) throws IOException {
        return readFont(new File(fontFile));
    }

    public static FVFont readFont(byte[] fontData) throws IOException {
        registerFontAdapters();

        // loop through and use first one to read without an error
        for (Class<? extends FVFont> adapterOn : adapters) {
            FVFont adapter = tryReadFontAdapter(fontData, adapterOn);
            if (adapter != null)
                return adapter;
        }

        // screwy double loop since CFF fonts don't have magic number header and was try catching around every font
        // adapter that matched in detectFormat, think just having CFF adapter always try last fixes this, but what if
        // a CFF starts with 'wOF2' or something? font-box bare CFF parser will often work on non CFF fonts anyway which
        // causes issues with that too, needa write own base CFF parser sometime.

        // if nothing can read go at it again and use the first one to throw an exception
        // as the exception message for debugging.
        for (Class<? extends FVFont> adapterOn : adapters) {

            try {
                FVFont adapter = parseFont(fontData, adapterOn);
                if (adapter != null)
                    return adapter;
            } catch (Exception ex) {
                throw new IOException("FontVerter could not read the given font file.", ex);
            }
        }

        throw new IOException("FontVerter could not detect the input font's type.");
    }

    private static FVFont tryReadFontAdapter(byte[] fontData, Class<? extends FVFont> adapterOn) throws IOException {
        try {
            return parseFont(fontData, adapterOn);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private static FVFont parseFont(byte[] fontData, Class<? extends FVFont> adapterOn) throws InstantiationException, IllegalAccessException, IOException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        FVFont adapter = adapterOn.getDeclaredConstructor().newInstance();
        if (adapter.detectFormat(fontData)) {
            adapter.read(fontData);
            return adapter;
        }

        return null;
    }

    private static void registerFontAdapters() {
        synchronized (adapterLock) {
            if (adapters == null) {
                Reflections reflections = new Reflections("org.mabb.fontverter");
                Set<Class<? extends FVFont>> adapterClasses = reflections.getSubTypesOf(FVFont.class);
                adapters = new ArrayList<>(adapterClasses);

                // CFF always last to try
                Class<? extends FVFont> cffAdapter = null;
                for (Class<? extends FVFont> adapterOn : adapters) {
                    if (adapterOn.getSimpleName().contains("CffFont")) {
                        cffAdapter = adapterOn;
                        break;
                    }
                }

                int cffIndex = adapters.indexOf(cffAdapter);
                adapters.set(cffIndex, adapters.get(adapters.size() - 1));
                adapters.set(adapters.size() - 1, cffAdapter);

                adapters = removeAbstractClasses(adapters);
            }
        }
    }

    private static List<Class<? extends FVFont>> removeAbstractClasses(List<Class<? extends FVFont>> classes) {
        List<Class<? extends FVFont>> filtered = new ArrayList<>();
        for (Class<? extends FVFont> adapterOn : classes) {
            if (!Modifier.isAbstract(adapterOn.getModifiers()))
                filtered.add(adapterOn);
        }

        return filtered;
    }
}
