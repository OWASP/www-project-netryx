package org.owasp.netryx.waf.util;

import org.owasp.netryx.waf.cache.LRUCache;

public final class AntPathMatcher {
    private final PathMatcher matcher;
    private final LRUCache<String, Boolean> cache;

    public AntPathMatcher(String pattern, int cacheSize) {
        this.matcher = new PathMatcher(pattern);
        this.cache = new LRUCache<>(cacheSize);
    }

    public AntPathMatcher(String pattern) {
        this(pattern, 128);
    }

    public boolean matches(String path) {
        return cache.computeIfAbsent(path, matcher::match);
    }

    public static AntPathMatcher of(String path) {
        return new AntPathMatcher(path);
    }
}