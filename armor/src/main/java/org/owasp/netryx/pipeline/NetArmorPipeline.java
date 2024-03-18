package org.owasp.netryx.pipeline;

import org.owasp.netryx.config.CommonSecurityConfig;
import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.fingerprint.tls.TlsFingerprintPacketHandler;
import org.owasp.netryx.intrusion.IntrusionDetector;
import org.owasp.netryx.limiter.BlacklistLimiter;
import org.owasp.netryx.limiter.WhitelistLimiter;
import org.owasp.netryx.mitigation.MitigationHandler;
import org.owasp.netryx.mitigation.fingerprint.TlsFingerprintHandler;
import org.owasp.netryx.mitigation.flood.HttpFloodHandler;
import org.owasp.netryx.mitigation.flood.RapidResetHandler;
import org.owasp.netryx.mitigation.intrusion.IntrusionMitigationHandler;
import org.owasp.netryx.mitigation.list.BlacklistHandler;
import org.owasp.netryx.mitigation.list.WhitelistHandler;
import org.owasp.netryx.mitigation.policy.HttpSecurityPolicyHandler;
import org.owasp.netryx.model.CommonConfig;
import org.owasp.netryx.provider.NettyServerPipeline;
import org.owasp.netryx.provider.NettyServerProvider;

import java.util.function.Supplier;

public class NetArmorPipeline<T> implements WebArmorPipeline<T> {
    private final SecurityConfig config;
    private final NettyServerPipeline<T> pipeline;

    public NetArmorPipeline(SecurityConfig config, NettyServerPipeline<T> pipeline) {
        this.config = config;
        this.pipeline = pipeline;
    }

    @Override
    public NettyServerPipeline<T> pipeline() {
        return pipeline;
    }

    public SecurityConfig getConfig() {
        return config;
    }

    public static <T> Builder<T> newBuilder(NettyServerProvider<T> provider) {
        return new Builder<>(provider.newPipeline());
    }

    public static class Builder<T> {
        private final NettyServerPipeline<T> pipelineConfigurer;
        private SecurityConfig config = new CommonSecurityConfig(new CommonConfig());

        public Builder(NettyServerPipeline<T> pipelineConfigurer) {
            this.pipelineConfigurer = pipelineConfigurer;
        }

        public Builder<T> config(SecurityConfig config) {
            this.config = config;
            return this;
        }

        public Builder<T> mitigation(Supplier<MitigationHandler> mitigationHandler) {
            pipelineConfigurer.addMitigationHandler(mitigationHandler);
            return this;
        }

        public Builder<T> whitelist(WhitelistLimiter limiter) {
            return mitigation(() -> new WhitelistHandler(limiter));
        }

        public Builder<T> blacklist(BlacklistLimiter limiter) {
            return mitigation(() -> new BlacklistHandler(limiter));
        }

        public Builder<T> tlsFingerprint(TlsFingerprintPacketHandler packetHandler) {
            return mitigation(() -> new TlsFingerprintHandler(packetHandler));
        }

        public Builder<T> intrusion(IntrusionDetector detector) {
            return mitigation(() -> new IntrusionMitigationHandler(detector));
        }

        public NetArmorPipeline<T> build() {
            applyBasicSecurity();
            return new NetArmorPipeline<>(config, pipelineConfigurer);
        }

        private void applyBasicSecurity() {
            if (config.enableSecurityPolicy())
                mitigation(() -> HttpSecurityPolicyHandler.getInstance(config));

            if (config.limitRapidReset()) {
                mitigation(() -> RapidResetHandler.getInstance(config));
            }

            if (config.limitRequests())
                mitigation(() -> HttpFloodHandler.getInstance(config));
        }
    }
}
