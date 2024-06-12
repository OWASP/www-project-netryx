package org.owasp.netryx.test;

import org.junit.jupiter.api.Test;
import org.owasp.netryx.constant.JavaScriptEncoding;
import org.owasp.netryx.encoder.impl.DefaultJavaScriptEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaScriptEncoderTest {
    @Test
    public void testSlash() {
        var encoder = new DefaultJavaScriptEncoder(JavaScriptEncoding.HTML, true);

        var input = "var re = /ab+c/;";
        var expected = "var re = \\x2Fab+c\\x2F;";
        var result = encoder.encode(input);

        assertEquals(expected, result);
    }

    @Test
    public void testEquals() {
        var encoder = new DefaultJavaScriptEncoder(JavaScriptEncoding.HTML, true);

        var input = "var str = \"name=John\";";
        var expected = "var str = \\x22name=John\\x22;";
        var result = encoder.encode(input);

        assertEquals(expected, result);
    }

    @Test
    public void testHyphen() {
        var encoder = new DefaultJavaScriptEncoder(JavaScriptEncoding.HTML, true);

        var input = "var str = \"a-b\";";
        var expected = "var str = \\x22a\\x2Db\\x22;";
        var result = encoder.encode(input);

        assertEquals(expected, result);
    }

    @Test
    public void testUnicodeCharacters() {
        var encoder = new DefaultJavaScriptEncoder(JavaScriptEncoding.HTML, true);

        var input = "var emoji = 'hi 😊';";
        var expected = "var emoji = \\x27hi \\uD83D\\uDE0A\\x27;";
        var result = encoder.encode(input);

        assertEquals(expected, result);
    }

    @Test
    public void testAmpersand() {
        var encoder = new DefaultJavaScriptEncoder(JavaScriptEncoding.ATTRIBUTE, true);

        var input = "var url = \"https://example.com?name=John&age=30\";";
        var expected = "var url = \\x22https://example.com?name=John\\x26age=30\\x22;";
        var result = encoder.encode(input);

        assertEquals(expected, result);
    }

    @Test
    public void testBackslash() {
        var encoder = new DefaultJavaScriptEncoder(JavaScriptEncoding.HTML, true);

        var input = "var path = \"C:\\path\\to\\file\";";
        var expected = "var path = \\x22C:\\\\path\\\\to\\\\file\\x22;";
        var result = encoder.encode(input);

        assertEquals(expected, result);
    }
}
