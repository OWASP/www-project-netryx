package org.owasp.netryx.mitigation.intrusion.model;

import io.netty.handler.codec.http.FullHttpRequest;
import org.owasp.netryx.constant.HttpProtocol;
import org.owasp.netryx.fingerprint.http2.AkamaiHttp2Fingerprint;
import org.owasp.netryx.fingerprint.request.Ja4hFingerprint;
import org.owasp.netryx.fingerprint.tls.Ja3Fingerprint;
import org.owasp.netryx.fingerprint.tls.Ja4Fingerprint;
import org.owasp.netryx.fingerprint.tls.packet.client.ClientHello;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the data of an intrusion detection.
 */
public class IntrusionDetectionData {
    private HttpProtocol httpProtocol = HttpProtocol.HTTP_1_1;
    private String remoteAddress;
    private FullHttpRequest request;
    private ClientHello clientHello;
    private AkamaiHttp2Fingerprint http2Fingerprint = new AkamaiHttp2Fingerprint();

    public IntrusionDetectionData() {}

    public IntrusionDetectionData(IntrusionDetectionData data) {
        this.httpProtocol = data.httpProtocol;
        this.remoteAddress = data.remoteAddress;
        this.request = data.request;
        this.clientHello = data.clientHello;
        this.http2Fingerprint = data.http2Fingerprint;
    }

    public HttpProtocol getHttpProtocol() {
        return httpProtocol;
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

    public Ja3Fingerprint getJa3Fingerprint() {
        return clientHello == null ? null : clientHello.ja3();
    }

    public Ja4Fingerprint getJa4Fingerprint() {
        return clientHello == null ? null : new Ja4Fingerprint(clientHello);
    }

    public Ja4hFingerprint getJa4HttpFingerprint() {
        if (request == null)
            return null;

        var headers = request.headers().entries().stream()
                .collect(
                    Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    )
                );

        var builder = Ja4hFingerprint.newBuilder()
                .httpMethod(request.method().name())
                .httpVersion(httpProtocol.getNumber());

        for (var header : request.headers())
            builder.addHeader(header.getKey(), header.getValue());

        return builder.build();
    }

    public AkamaiHttp2Fingerprint getHttp2Fingerprint() {
        return http2Fingerprint;
    }

    public void setHttpProtocol(HttpProtocol httpProtocol) {
        this.httpProtocol = httpProtocol;
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
