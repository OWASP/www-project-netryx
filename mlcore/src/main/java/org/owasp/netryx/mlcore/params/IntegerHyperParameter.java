package org.owasp.netryx.mlcore.params;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerHyperParameter extends AbstractHyperParameter<Integer> {
    public IntegerHyperParameter(Integer value, String name) {
        super(value, name);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(getValue());
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        setValue(in.readInt());
    }
}