package org.owasp.netryx.password.config;

/**
 * Configuration settings for the BCrypt hashing algorithm.
 * <p>
 * This class provides a configuration for BCrypt, specifically the cost parameter
 * that controls the algorithm's complexity and, by extension, its security and execution time.
 */
public class BCryptConfig {
    private final int cost;

    /**
     * Constructs a new BCryptConfig with the default cost parameter.
     */
    public BCryptConfig() {
        this(12); // Default value
    }

    /**
     * Constructs a new BCryptConfig with a specified cost parameter.
     *
     * @param cost The cost parameter of the algorithm, controlling the complexity.
     */
    public BCryptConfig(int cost) {
        this.cost = cost;
    }

    /**
     * Returns the cost parameter of the BCrypt algorithm.
     *
     * @return the cost parameter.
     */
    public int getCost() {
        return cost;
    }
}


