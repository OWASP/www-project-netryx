package org.owasp.netryx.mitigation.intrusion.constant;

public final class HandlerName {
    private HandlerName() {}

    public static final String REMOTE_ADDRESS = "remote-address-collector";
    public static final String TLS_FINGERPRINT = "tls-fingerprint-collector";
    public static final String HTTP2_FINGERPRINT = "http2-fingerprint-collector";
    public static final String HTTP_PROTOCOL_DETECTOR = "http-protocol-detector";
}
