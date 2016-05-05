package org.fontverter;

import org.apache.commons.io.FileUtils;
import org.fontverter.opentype.OtfFontAdapter;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class FontVerter {
    private static Set<Class<? extends FontAdapter>> adapters;
    private static final Object adapterLock = new Object();

    public enum FontFormat {
        CFF,
        OTF,
        WOFF
    }

    public static FontAdapter convertFont(byte[] inputFontData, FontFormat convertTo) throws IOException {
        FontAdapter inputFont = readFont(inputFontData);
        FontConverter converter = inputFont.createConverterForType(convertTo);

        return converter.convertFont(inputFont);
    }

    public static FontAdapter convertFont(File inputFontData, FontFormat convertTo) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(inputFontData);

        return convertFont(data, convertTo);
    }

    public static FontAdapter readFont(byte[] fontData) throws IOException {
        registerFontAdapters();

        for (Class<? extends FontAdapter> adapterOn : adapters) {
            FontAdapter adapter = tryReadFontAdapter(fontData, adapterOn);
            if (adapter != null) return adapter;
        }

        return new OtfFontAdapter();
    }

    public static FontAdapter readFont(File fontFile) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(fontFile);
        return readFont(data);
    }

    public static FontAdapter readFont(String fontFile) throws IOException {
        return readFont(new File(fontFile));
    }

    private static FontAdapter tryReadFontAdapter(byte[] fontData, Class<? extends FontAdapter> adapterOn) throws IOException {
        try {
            FontAdapter adapter = adapterOn.newInstance();
            if(adapter.detectFormat(fontData)) {
                adapter.read(fontData);
                return adapter;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private static void registerFontAdapters() {
        synchronized (adapterLock) {
            if(adapters == null) {
                Reflections reflections = new Reflections("org.fontverter");
                adapters = reflections.getSubTypesOf(FontAdapter.class);
            }
        }
    }

}
