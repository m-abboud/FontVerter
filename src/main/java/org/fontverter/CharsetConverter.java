package org.fontverter;

import org.apache.fontbox.encoding.Encoding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharsetConverter {
    public static Map<Integer, Integer> nameMapToEncoding(Map<Integer, String> idToNames, Encoding encoding) {
        Map<Integer, Integer> idToEncoding = new HashMap<Integer, Integer>();
        for(Map.Entry<Integer, String> nameSetOn : idToNames.entrySet()) {
            int charCode = encoding.getCode(nameSetOn.getValue());
            int glyphId = nameSetOn.getKey();
            idToEncoding.put(charCode, glyphId);
        }

        return idToEncoding;
    }
}
