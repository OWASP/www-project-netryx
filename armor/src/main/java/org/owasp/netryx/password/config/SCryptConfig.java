package org.owasp.netryx.password.config;

/**
 * Configuration settings for SCrypt hashing algorithm.
 * <p>
 * Provides configuration parameters for the SCrypt algorithm, including cost, block size, parallelism, salt length, and hash length.
 */
public class SCryptConfig {
    private final int cost;
    private final int blockSize;
    private final int parallelism;
    private final int saltLength;
    private final int hashLength;

    /**
     * Constructs a new SCryptConfig with default parameters.
     */
    public SCryptConfig() {
        this(16384, 8, 1, 16, 32);
    }

    /**
     * Constructs a new SCryptConfig with specified parameters.
     *
     * @param cost        Cost parameter N, which is a CPU/memory cost parameter.
     * @param blockSize   Block size parameter r, which specifies the block size.
     * @param parallelism Parallelization parameter p, which is a parallelization parameter.
     * @param saltLength  Salt length in bytes.
     * @param hashLength  Hash length in bytes.
     */
    public SCryptConfig(int cost, int blockSize, int parallelism, int saltLength, int hashLength) {
        this.cost = cost;
        this.blockSize = blockSize;
        this.parallelism = parallelism;
        this.saltLength = saltLength;
        this.hashLength = hashLength;
    }

    // Getter methods

    /**
     * Returns the cost parameter N.
     *
     * @return the cost parameter.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Returns the block size parameter r.
     *
     * @return the block size.
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * Returns the parallelization parameter p.
     *
     * @return the parallelism.
     */
    public int getParallelism() {
        return parallelism;
    }

    /**
     * Returns the salt length in bytes.
     *
     * @return the salt length.
     */
    public int getSaltLength() {
        return saltLength;
    }

    /**
     * Returns the hash length in bytes.
     *
     * @return the hash length.
     */
    public int getHashLength() {
        return hashLength;
    }
}

