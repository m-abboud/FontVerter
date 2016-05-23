package org.fontverter;

import org.apache.commons.io.FileUtils;
import org.fontverter.cff.CffFontAdapter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class FontVerter {
    private static Logger log = LoggerFactory.getLogger(FontVerter.class);
    private static Class[] adapters;
    private static final Object adapterLock = new Object();

    public enum FontFormat {
        CFF,
        OTF,
        WOFF1,
        WOFF2
    }

    public static FVFont convertFont(byte[] inputFontData, FontFormat convertTo) throws IOException {
        FVFont inputFont = readFont(inputFontData);
        FontConverter converter = inputFont.createConverterForType(convertTo);

        return converter.convertFont(inputFont);
    }

    public static FVFont convertFont(File inputFontData, FontFormat convertTo) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(inputFontData);
        return convertFont(data, convertTo);
    }

    public static FVFont convertFont(String inputFontData, FontFormat convertTo) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(new File(inputFontData));
        return convertFont(data, convertTo);
    }

    public static FVFont readFont(byte[] fontData) throws IOException {
        registerFontAdapters();

        for (Class<? extends FVFont> adapterOn : adapters) {
            FVFont adapter = tryReadFontAdapter(fontData, adapterOn);
            if (adapter != null)
                return adapter;
        }

        FVFont adapter = new CffFontAdapter();
        adapter.read(fontData);
        return adapter;
    }

    public static FVFont readFont(File fontFile) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(fontFile);
        return readFont(data);
    }

    public static FVFont readFont(String fontFile) throws IOException {
        return readFont(new File(fontFile));
    }

    private static FVFont tryReadFontAdapter(byte[] fontData, Class<? extends FVFont> adapterOn) throws IOException {
        try {
            FVFont adapter = adapterOn.newInstance();

            if (adapter.detectFormat(fontData)) {
                adapter.read(fontData);
                return adapter;
            }
        } catch (Exception e) {
            log.debug("Error creating font adapter {} Message: {}", adapterOn.getName(), e);
            return null;
        }

        return null;
    }

    private static void registerFontAdapters() {
        synchronized (adapterLock) {
            if (adapters == null) {
                Reflections reflections = new Reflections("org.fontverter");
                Set<Class<? extends FVFont>> adapterClasses = reflections.getSubTypesOf(FVFont.class);
                adapters = adapterClasses.toArray(new Class[adapterClasses.size()]);
            }
        }
    }

}
