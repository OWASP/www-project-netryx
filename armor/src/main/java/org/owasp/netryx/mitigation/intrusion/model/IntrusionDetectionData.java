package org.owasp.netryx.mitigation.intrusion.model;

import io.netty.handler.codec.http.FullHttpRequest;
import org.owasp.netryx.fingerprint.http2.AkamaiHttp2Fingerprint;
import org.owasp.netryx.fingerprint.tls.packet.client.ClientHello;

/**
 * Represents the data of an intrusion detection.
 */
public class IntrusionDetectionData {
    private String remoteAddress;
    private FullHttpRequest request;
    private ClientHello clientHello;
    private AkamaiHttp2Fingerprint http2Fingerprint = new AkamaiHttp2Fingerprint();

    public IntrusionDetectionData() {}

    public IntrusionDetectionData(IntrusionDetectionData data) {
        this.remoteAddress = data.remoteAddress;
        this.request = data.request;
        this.clientHello = data.clientHello;
        this.http2Fingerprint = data.http2Fingerprint;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public ClientHello getClientHello() {
        return clientHello;
    }

    public AkamaiHttp2Fingerprint getHttp2Fingerprint() {
        return http2Fingerprint;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public void setClientHello(ClientHello clientHello) {
        this.clientHello = clientHello;
    }
}
