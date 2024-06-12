package org.owasp.netryx;

import org.owasp.memory.allocator.MemoryAllocator;
import org.owasp.netryx.encoder.EncoderProvider;
import org.owasp.netryx.validator.ValidatorProvider;

public interface WebArmor {
    ValidatorProvider validator();

    EncoderProvider encoder();

    MemoryAllocator memory();
}
