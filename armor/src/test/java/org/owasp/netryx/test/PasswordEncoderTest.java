package org.owasp.netryx.test;

import org.junit.jupiter.api.Test;
import org.owasp.netryx.NetArmor;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncoderTest {
    private final NetArmor armor = NetArmor.create();

    @Test
    public void assertPasswordMatches() {
        var password = "my-very-unsafe-password";
        var encoded = armor.password().encode(password);

        assertTrue(armor.password().matches(password, encoded));
    }
}
