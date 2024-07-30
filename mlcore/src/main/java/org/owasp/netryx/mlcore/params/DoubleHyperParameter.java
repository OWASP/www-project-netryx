package org.owasp.netryx.mlcore.params;

import org.owasp.netryx.mlcore.params.AbstractHyperParameter;

public class DoubleHyperParameter extends AbstractHyperParameter<Double> {
    public DoubleHyperParameter(Double value, String name) {
        super(value, name);
    }
}