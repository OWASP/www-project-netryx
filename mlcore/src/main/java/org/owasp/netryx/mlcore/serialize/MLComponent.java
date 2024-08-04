package org.owasp.netryx.mlcore.serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface MLComponent {
    void save(DataOutputStream out) throws IOException;

    void load(DataInputStream in) throws IOException;
}
