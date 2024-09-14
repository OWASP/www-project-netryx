package org.owasp.netryx.intrusion;

import org.owasp.netryx.constant.HandleCode;
import org.owasp.netryx.constant.IntrusionPhase;
import org.owasp.netryx.mitigation.intrusion.IntrusionChannelHandler;
import org.owasp.netryx.mitigation.intrusion.collector.Http2FingerprintCollector;
import org.owasp.netryx.mitigation.intrusion.collector.RemoteAddressCollector;
import org.owasp.netryx.mitigation.intrusion.collector.TlsFingerprintCollector;
import org.owasp.netryx.mitigation.intrusion.model.IntrusionDetectionData;
import reactor.core.publisher.Mono;

/**
 * IntrusionDetector
 * Base interface for intrusion detection
 * <p>
 * Allows to detect malicious requests based on the remote address, JA3 fingerprint and HTTP request,
 * that is more than enough to detect most of the attacks and even unique ones,
 * depending on the implementation or AI model.
 * <p>
 * There is no default implementation, because it is up to the user to decide how to detect attacks,
 * but we are working on a common implementation, that will be available in the future as
 * a separate module using machine learning in order to follow Security by Default principle.
 * <p>
 * Data is collected in 3 steps:
 * <p>
 * @see RemoteAddressCollector
 * @see TlsFingerprintCollector
 * @see Http2FingerprintCollector
 * <p>
 * After this, finally the intrusion detection is performed.
 * @see IntrusionChannelHandler
 */
public interface IntrusionDetector {
    Mono<DetectionResult> detect(IntrusionPhase phase, IntrusionDetectionData data);

    // Will be called if the request is detected as malicious or suspicious
    Mono<HandleCode> onDetected(DetectionResult result);

    // Handler name. Should be unique
    String name();
}
