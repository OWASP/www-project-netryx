package org.owasp.netryx.mlcore.params;

public interface HyperParameter<T> {
    void setValue(T value);

    T getValue();

    String getName();
}