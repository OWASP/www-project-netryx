package org.owasp.netryx.test;

import org.junit.jupiter.api.Test;
import org.owasp.netryx.encoder.impl.DefaultLDAPEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LDAPEncoderTest {
    private final DefaultLDAPEncoder encoder = new DefaultLDAPEncoder();

    @Test
    public void testEncodeEmptyString() {
        assertEquals("", encoder.encode(""));
    }

    @Test
    public void testEncodeStringWithoutSpecialCharacters() {
        assertEquals("SafeString", encoder.encode("SafeString"));
    }

    @Test
    public void testEncodeStringWithSpecialCharacters() {
        assertEquals("Oh\\5cMyGod", encoder.encode("Oh\\MyGod"));
        assertEquals("John\\2aDoe", encoder.encode("John*Doe"));
        assertEquals("admin\\28user\\29", encoder.encode("admin(user)"));
        assertEquals("user\\2fname", encoder.encode("user/name"));
        assertEquals("null\\00char", encoder.encode("null\0char"));
        assertEquals("test\\23user", encoder.encode("test#user"));
        assertEquals("first\\2blast", encoder.encode("first+last"));
        assertEquals("less\\3cgreat", encoder.encode("less<great"));
        assertEquals("great\\3eless", encoder.encode("great>less"));
        assertEquals("comma\\2cname", encoder.encode("comma,name"));
        assertEquals("semi\\3bcolon", encoder.encode("semi;colon"));
        assertEquals("quote\\22test", encoder.encode("quote\"test"));
        assertEquals("equal\\3dtest", encoder.encode("equal=test"));
    }

    @Test
    public void testEncodeStringWithControlCharacters() {
        assertEquals("hello\\7fworld", encoder.encode("hello\u007fworld"));
    }

    @Test
    public void testEncodeStringWithUnicodeCharacters() {
        assertEquals("hello\\c3\\a9world", encoder.encode("helloéworld")); // é
        assertEquals("hello\\e2\\82\\acworld", encoder.encode("hello€world")); // €
    }
}