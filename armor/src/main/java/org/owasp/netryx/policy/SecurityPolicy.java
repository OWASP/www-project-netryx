package org.owasp.netryx.policy;

import io.netty.handler.codec.http.HttpResponse;

/**
 * SecurityPolicy
 * Interface for security policies
 * <p>
 * Security policies are important for preventing attacks such as clickjacking, XSS, and MIME-sniffing
 */
public interface SecurityPolicy {
    void apply(HttpResponse response);
}
