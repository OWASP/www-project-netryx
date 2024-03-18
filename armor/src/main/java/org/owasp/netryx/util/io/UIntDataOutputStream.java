package org.owasp.netryx.util.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class UIntDataOutputStream extends DataOutputStream implements UIntOutputStream {
    public UIntDataOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void writeBytes(byte[] bytes) throws IOException {
        out.write(bytes);
    }
}
