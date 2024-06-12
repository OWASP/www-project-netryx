package org.owasp.netryx.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.owasp.netryx.constant.HtmlEncoding;
import org.owasp.netryx.encoder.impl.DefaultHtmlEncoder;
import org.owasp.netryx.encoder.config.HtmlEncoderConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlEncoderTest {
    private DefaultHtmlEncoder encoder;

    @BeforeEach
    void setUp() {
        var config = HtmlEncoderConfig.withMode(HtmlEncoding.ALL);
        encoder = new DefaultHtmlEncoder(config);
    }

    @Test
    void testEncodeBasicHtmlCharacters() {
        var input = "<script>alert('xss')</script>";
        var expected = "&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;";

        assertEquals(expected, encoder.encode(input));
    }

    @Test
    void testEncodeSpecialCharacters() {
        var input = "&\"'<>=`";
        var expected = "&amp;&quot;&#39;&lt;&gt;&#x3D;&#x60;";
        assertEquals(expected, encoder.encode(input));
    }

    @Test
    void testEncodeSurrogatePairs() {
        var input = "😊";
        var expected = "&#128522;";
        assertEquals(expected, encoder.encode(input));
    }

    @Test
    void testEncodeInvalidCharacters() {
        var input = "\u0000\u0008\u001F";
        var expected = "&#xfffd;&#xfffd;&#xfffd;";
        assertEquals(expected, encoder.encode(input));
    }

    @Test
    void testSanitizeHtml() {
        var input = "<div>Safe content</div><script>alert('xss')</script>";
        var expected = "<div>Safe content</div>";
        assertEquals(expected, encoder.sanitize(input));
    }

    @Test
    void testSanitizeInvalidHtml() {
        var input = "<div>Invalid < tag</div>";
        var expected = "<div>Invalid &lt; tag</div>";
        assertEquals(expected, encoder.sanitize(input));
    }
}
