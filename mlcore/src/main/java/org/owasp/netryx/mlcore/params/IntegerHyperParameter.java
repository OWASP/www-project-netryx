package org.owasp.netryx.mlcore.params;

import org.owasp.netryx.mlcore.params.AbstractHyperParameter;

public class IntegerHyperParameter extends AbstractHyperParameter<Integer> {
    public IntegerHyperParameter(Integer value, String name) {
        super(value, name);
    }
}