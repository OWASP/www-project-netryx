package org.owasp.netryx.exception;

public class NotConfiguredFieldException extends RuntimeException {
    private final String name;

    public NotConfiguredFieldException(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
