package org.owasp.netryx;

import org.owasp.netryx.memory.allocator.DefaultMemoryAllocator;
import org.owasp.netryx.memory.allocator.MemoryAllocator;
import org.owasp.netryx.config.CommonSecurityConfig;
import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.encoder.DefaultEncoderProvider;
import org.owasp.netryx.encoder.EncoderProvider;
import org.owasp.netryx.model.CommonConfig;
import org.owasp.netryx.validator.DefaultValidatorProvider;
import org.owasp.netryx.validator.ValidatorProvider;

/**
 * NetArmor is the main class of armor framework.
 * In case you want to create own custom Armor instance with custom features:
 * @see WebArmor
 */
public class NetArmor implements WebArmor {
    private final ValidatorProvider validator;
    private final EncoderProvider encoder;
    private final MemoryAllocator memoryAllocator;

    private NetArmor(SecurityConfig securityConfig) {
        this.validator = new DefaultValidatorProvider(securityConfig);
        this.encoder = new DefaultEncoderProvider();

        this.memoryAllocator = new DefaultMemoryAllocator();
    }

    @Override
    public ValidatorProvider validator() {
        return validator;
    }

    @Override
    public EncoderProvider encoder() {
        return encoder;
    }

    @Override
    public MemoryAllocator memory() {
        return memoryAllocator;
    }

    public static NetArmor create() {
        return create(new CommonConfig());
    }

    public static NetArmor create(CommonConfig config) {
        return create(new CommonSecurityConfig(config));
    }

    public static NetArmor create(SecurityConfig config) {
        return new NetArmor(config);
    }
}
