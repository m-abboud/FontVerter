package org.mabb.fontverter.eot;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class TestEotHeader {
    @Test
    public void emptyHeaderNamesRemainEmpty() {
        EotHeader header = new EotHeader();
        header.familyName = new Byte[0];
        header.rootString = new Byte[0];
        header.fullName = new Byte[0];
        header.versionName = new Byte[0];
        header.styleName = new Byte[0];
        assertEquals("", header.getFamilyName());
        assertEquals("", header.getRootName());
        assertEquals("", header.getFullName());
        assertEquals("", header.getVersionName());
        assertEquals("", header.getStyleName());
    }

    @Test
    public void decodesUnicodeNamesThroughEveryGetter() {
        String name = "Font \u00e9\u97d3\ud83d\ude00";
        EotHeader header = new EotHeader();
        Byte[] bytes = box(name.getBytes(StandardCharsets.UTF_16));
        header.familyName = bytes;
        header.rootString = bytes;
        header.fullName = bytes;
        header.versionName = bytes;
        header.styleName = bytes;
        assertEquals(name, header.getFamilyName());
        assertEquals(name, header.getRootName());
        assertEquals(name, header.getFullName());
        assertEquals(name, header.getVersionName());
        assertEquals(name, header.getStyleName());
    }

    @Test
    public void preservesAllByteValues() {
        byte[] bytes = new byte[256];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) i;
        }
        assertDecoded(bytes);
    }

    @Test
    public void preservesBomAndMalformedUtf16Decoding() {
        assertDecoded(new byte[] {(byte) 0xff, (byte) 0xfe, 65, 0});
        assertDecoded(new byte[] {(byte) 0xfe, (byte) 0xff, 0, 65});
        assertDecoded(new byte[] {0, 65, 66});
        assertDecoded(new byte[] {(byte) 0xd8, 0});
        assertDecoded(new byte[] {(byte) 0xdc, 0});
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullArray() {
        EotHeader header = new EotHeader();
        header.fullName = null;
        header.getFullName();
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullElement() {
        EotHeader header = new EotHeader();
        header.fullName = new Byte[] {0, null};
        header.getFullName();
    }

    private static void assertDecoded(byte[] bytes) {
        EotHeader header = new EotHeader();
        header.fullName = box(bytes);
        assertEquals(new String(bytes, StandardCharsets.UTF_16), header.getFullName());
    }

    private static Byte[] box(byte[] bytes) {
        Byte[] boxed = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            boxed[i] = bytes[i];
        }
        return boxed;
    }
}
