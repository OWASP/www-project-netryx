package org.owasp.netryx.fingerprint.constant;

/**
 * ResultCode
 * Result code for TLS fingerprint handlers
 * <p>
 * OK - the fingerprint is allowed
 * BLOCK - the fingerprint is blocked
 * <p>
 * When blocked, the ChannelHandlerContext will be closed.
 */
public enum ResultCode {
    OK,
    BLOCK
}
