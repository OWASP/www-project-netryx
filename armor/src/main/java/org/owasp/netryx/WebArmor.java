package org.owasp.netryx;

import org.owasp.memory.allocator.MemoryAllocator;
import org.owasp.netryx.encoder.HtmlEncoder;
import org.owasp.netryx.password.PasswordEncoder;
import org.owasp.netryx.validator.Validator;
import org.owasp.validator.html.Policy;

public interface WebArmor {
    Validator validator();

    HtmlEncoder htmlEncoder();

    HtmlEncoder htmlEncoder(Policy policy);

    PasswordEncoder password();

    MemoryAllocator memory();
}
