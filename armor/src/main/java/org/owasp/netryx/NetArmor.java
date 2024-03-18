package org.owasp.netryx;

import org.owasp.memory.allocator.DefaultMemoryAllocator;
import org.owasp.memory.allocator.MemoryAllocator;
import org.owasp.netryx.config.CommonSecurityConfig;
import org.owasp.netryx.config.SecurityConfig;
import org.owasp.netryx.encoder.DefaultHtmlEncoder;
import org.owasp.netryx.encoder.HtmlEncoder;
import org.owasp.netryx.model.CommonConfig;
import org.owasp.netryx.password.ArgonPasswordEncoder;
import org.owasp.netryx.password.BCryptPasswordEncoder;
import org.owasp.netryx.password.PasswordEncoder;
import org.owasp.netryx.password.SCryptPasswordEncoder;
import org.owasp.netryx.validator.DefaultValidator;
import org.owasp.netryx.validator.Validator;
import org.owasp.validator.html.Policy;

/**
 * NetArmor is the main class of armor framework.
 * In case you want to create own custom Armor instance with custom features:
 * @see WebArmor
 */
public class NetArmor implements WebArmor {
    private final Validator validator;
    private final HtmlEncoder htmlEncoder;
    private final MemoryAllocator memoryAllocator;
    private final PasswordEncoder passwordEncoder;

    private NetArmor(SecurityConfig config) {
        this.validator = new DefaultValidator(config);
        this.htmlEncoder = new DefaultHtmlEncoder();
        this.memoryAllocator = new DefaultMemoryAllocator();

        switch (config.encoderType()) {
            case BCRYPT: {
                this.passwordEncoder = new BCryptPasswordEncoder(config.bcryptConfig());
                break;
            }
            case ARGON: {
                this.passwordEncoder = new ArgonPasswordEncoder(config.argonConfig());
                break;
            }
            case SCRYPT: {
                this.passwordEncoder = new SCryptPasswordEncoder(config.scryptConfig());
                break;
            }
            default: throw new IllegalArgumentException("Unsupported password encoder");
        };
    }

    @Override
    public Validator validator() {
        return validator;
    }

    @Override
    public HtmlEncoder htmlEncoder() {
        return htmlEncoder;
    }

    // There may be different policies for different pages.
    @Override
    public HtmlEncoder htmlEncoder(Policy policy) {
        return new DefaultHtmlEncoder(policy);
    }

    @Override
    public PasswordEncoder password() {
        return passwordEncoder;
    }

    @Override
    public MemoryAllocator memory() {
        return memoryAllocator;
    }

    public static NetArmor create() {
        return create(new CommonSecurityConfig(new CommonConfig()));
    }

    public static NetArmor create(SecurityConfig config) {
        return new NetArmor(config);
    }
}
