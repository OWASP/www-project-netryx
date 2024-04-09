package org.owasp.netryx.fingerprint.tls;

import java.util.List;

public interface TlsFingerprint {
    List<Integer> getCipherSuites();

    List<Integer> getNamedGroups();

    List<Integer> getExtensions();

    List<Integer> getPointFormats();

    String getRaw();

    String getHash();
}
