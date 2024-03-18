package org.owasp.netryx.password.config;

/**
 * Configuration settings for the Argon2 hashing algorithm.
 * <p>
 * This class encapsulates configuration parameters for the Argon2 algorithm, including memory cost,
 * parallelism, number of iterations, salt length, and hash length. These parameters can be tuned to
 * balance between security and performance based on specific requirements.
 */
public class ArgonConfig {
    private final int memoryCost;
    private final int parallelism;
    private final int iterations;
    private final int saltLength;
    private final int hashLength;

    /**
     * Constructs a new ArgonConfig with default parameters.
     */
    public ArgonConfig() {
        this(65536, 1, 4, 16, 32); // Default values
    }

    /**
     * Constructs a new ArgonConfig with specified parameters.
     *
     * @param memoryCost   The memory cost parameter m, defining the memory usage.
     * @param parallelism  The parallelism parameter p, defining the number of threads to use.
     * @param iterations   The number of iterations t, defining the complexity.
     * @param saltLength   The length of the salt in bytes.
     * @param hashLength   The length of the hash output in bytes.
     */
    public ArgonConfig(int memoryCost, int parallelism, int iterations, int saltLength, int hashLength) {
        this.memoryCost = memoryCost;
        this.parallelism = parallelism;
        this.iterations = iterations;
        this.saltLength = saltLength;
        this.hashLength = hashLength;
    }

    /**
     * Returns the memory cost parameter.
     *
     * @return The memory cost.
     */
    public int getMemoryCost() {
        return memoryCost;
    }

    /**
     * Returns the parallelism parameter.
     *
     * @return The parallelism.
     */
    public int getParallelism() {
        return parallelism;
    }

    /**
     * Returns the number of iterations.
     *
     * @return The iterations.
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * Returns the salt length in bytes.
     *
     * @return The salt length.
     */
    public int getSaltLength() {
        return saltLength;
    }

    /**
     * Returns the hash length in bytes.
     *
     * @return The hash length.
     */
    public int getHashLength() {
        return hashLength;
    }
}