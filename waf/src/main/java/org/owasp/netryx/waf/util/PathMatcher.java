package org.owasp.netryx.waf.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class PathMatcher {
    private final List<Segment> patternSegments;

    public PathMatcher(String pattern) {
        patternSegments = Arrays.stream(pattern.split("/"))
                .map(Segment::new)
                .collect(Collectors.toList());
    }

    public boolean match(String path) {
        var pathSegments = path.split("/");

        var patternIdx = 0;
        var pathIdx = 0;

        while (patternIdx < patternSegments.size() && pathIdx <= pathSegments.length) {
            var patternSegment = patternSegments.get(patternIdx);

            if (patternSegment.isDoubleWildcard) {
                patternIdx++;
                if (patternIdx == patternSegments.size()) return true;
                while (pathIdx < pathSegments.length && !pathSegments[pathIdx].equals(patternSegments.get(patternIdx).value))
                    pathIdx++;
            } else if (pathIdx < pathSegments.length) {
                if (patternSegment.matches(pathSegments[pathIdx])) {
                    patternIdx++;
                    pathIdx++;
                } else {
                    return false;
                }
            } else {
                break;
            }
        }

        return patternIdx == patternSegments.size() && pathIdx == pathSegments.length;
    }

    private static class Segment {
        final String value;
        final boolean isWildcard;
        final boolean isDoubleWildcard;

        Segment(String value) {
            this.value = value;
            this.isWildcard = "*".equals(value);
            this.isDoubleWildcard = "**".equals(value);
        }

        boolean matches(String segment) {
            return isWildcard || isDoubleWildcard || value.equals(segment);
        }
    }
}