package org.owasp.netryx.fingerprint.tls.packet;

/**
 * RecordPacket
 * Represents a TLS packet, containing a record
 */
public interface RecordPacket extends TlsPacket {
    Record getRecord();
}
