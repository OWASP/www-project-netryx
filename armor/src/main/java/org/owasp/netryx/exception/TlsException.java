package org.owasp.netryx.exception;

/**
 * TlsException
 * Exception thrown when TLS fingerprinting fails.
 */
public class TlsException extends RuntimeException {
    public TlsException(Exception e) {
        super(e);
    }
}
