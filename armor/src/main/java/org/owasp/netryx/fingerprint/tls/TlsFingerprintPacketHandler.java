package org.owasp.netryx.fingerprint.tls;

import io.netty.channel.ChannelHandlerContext;
import org.owasp.netryx.fingerprint.constant.ResultCode;
import org.owasp.netryx.fingerprint.tls.packet.client.ClientHello;
import reactor.core.publisher.Mono;

/**
 * FingerPrintPacketHandler
 * Interface for TLS fingerprint handlers
 * <p>
 * TLS fingerprint handlers are used to determine whether a TLS fingerprint should be blocked
 * <p>
 * It can be used for gathering information about the TLS fingerprint
 * or any other purpose. Please, follow the privacy policy according to regulations.
 */
public interface TlsFingerprintPacketHandler {
    Mono<ResultCode> handle(ChannelHandlerContext ctx, ClientHello ch);

    // Handler name. Should be unique.
    String name();
}
