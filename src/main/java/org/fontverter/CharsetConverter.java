package org.fontverter;

import org.apache.fontbox.encoding.Encoding;

import java.util.*;

public class CharsetConverter {
    public static Map<Integer, Integer> nameMapToEncoding(Map<Integer, String> idToNames, Encoding encoding) {
        Map<Integer, Integer> idToEncoding = new HashMap<Integer, Integer>();
        Map<Integer, Integer> usedCodes = new HashMap<Integer, Integer>();

        for(Map.Entry<Integer, String> nameSetOn : idToNames.entrySet()) {
            int charCode = nameToCode(nameSetOn.getValue(), encoding, usedCodes);
            int glyphId = nameSetOn.getKey();

            idToEncoding.put(charCode, glyphId);
        }

        return idToEncoding;
    }

    private static int nameToCode(String name,Encoding encoding, Map<Integer, Integer> usedCodes) {
        int code = 0;
        for (Map.Entry<Integer, String> entryOn : encoding.getCodeToNameMap().entrySet()) {
            int codeOn = entryOn.getKey();

            if(entryOn.getValue().equals(name) && !usedCodes.containsKey(codeOn))
                code = entryOn.getKey();
        }

        // have to remove char codes since can repeat in encoding map and names so need to move to next
        usedCodes.put(code, 0);
        return code;
    }
}
