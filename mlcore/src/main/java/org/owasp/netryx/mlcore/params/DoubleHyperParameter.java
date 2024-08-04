package org.owasp.netryx.mlcore.params;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleHyperParameter extends AbstractHyperParameter<Double> {
    public DoubleHyperParameter(Double value, String name) {
        super(value, name);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeDouble(getValue());
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        setValue(in.readDouble());
    }
}