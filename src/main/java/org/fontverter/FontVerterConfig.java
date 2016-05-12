package org.fontverter;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class FontVerterConfig implements Serializable {
    private static FontVerterConfig globalInstance;
    private static final Object lock = new Object();

    public static FontVerterConfig globalConfig() {
        synchronized (lock) {
            if (globalInstance == null)
                globalInstance = new FontVerterConfig();
            return SerializationUtils.clone(globalInstance);
        }
    }

    public static void setGlobalConfig(FontVerterConfig config) {
        synchronized (lock) {
            globalInstance = config;
        }
    }

}
