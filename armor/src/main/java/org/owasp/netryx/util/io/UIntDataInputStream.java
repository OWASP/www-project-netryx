package org.owasp.netryx.util.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

public class UIntDataInputStream extends DataInputStream implements UIntInputStream {
    public UIntDataInputStream(byte[] in) {
        super(new ByteArrayInputStream(in));
    }

    public UIntDataInputStream(InputStream in) {
        super(in);
    }
}
