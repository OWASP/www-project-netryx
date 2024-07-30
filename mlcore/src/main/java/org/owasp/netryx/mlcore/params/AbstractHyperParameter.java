package org.owasp.netryx.mlcore.params;

public abstract class AbstractHyperParameter<T> implements HyperParameter<T> {
    private T value;
    private final String name;

    protected AbstractHyperParameter(T value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }
}