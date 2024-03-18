package org.owasp.netryx.fingerprint.tls.packet;

import java.util.Arrays;

public interface TlsPacket {
    byte[] toByteArray();

    default int length() {
        return toByteArray().length;
    }

    static int sizeOf(TlsPacket... packets) {
        return Arrays.stream(packets).map(TlsPacket::length).mapToInt(Integer::intValue).sum();
    }
}
