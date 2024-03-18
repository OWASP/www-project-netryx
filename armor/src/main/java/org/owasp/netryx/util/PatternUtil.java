package org.owasp.netryx.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * PatternUtils
 * Utility class for loading default patterns
 */
public class PatternUtil {
    private PatternUtil() {}

    public static Map<String, String> loadDefaults() {
        try (var stream = PatternUtil.class
                .getClassLoader()
                .getResourceAsStream("default_patterns.properties")) {

            var properties = new Properties();
            properties.load(stream);

            var map = new HashMap<String, String>();
            properties.forEach((key, value) -> map.put((String) key, (String) value));

            return map;
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't load defaults patterns", e);
        }
    }
}
