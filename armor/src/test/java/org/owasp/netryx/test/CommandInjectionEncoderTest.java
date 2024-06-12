package org.owasp.netryx.test;

import org.junit.jupiter.api.Test;
import org.owasp.netryx.encoder.impl.DefaultCommandEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandInjectionEncoderTest {
    private final DefaultCommandEncoder encoder = new DefaultCommandEncoder();

    @Test
    public void testEncodeEmptyString() {
        assertEquals("", encoder.encode(""));
    }

    @Test
    public void testEncodeSafeString() {
        assertEquals("safe-command", encoder.encode("safe-command"));
    }

    @Test
    public void testEncodeUnsafeString() {
        assertEquals("unsafe\\;command", encoder.encode("unsafe;command"));
    }

    @Test
    public void testEncodeStringWithMultipleUnsafeCharacters() {
        assertEquals("unsafe\\;command\\&test", encoder.encode("unsafe;command&test"));
    }

    @Test
    public void testEncodeStringWithAllSpecialCharacters() {
        assertEquals("\\&\\|\\;\\$\\>\\<\\\\\\\"\\'\\*", encoder.encode("&|;$><\\\"'*"));
    }
}