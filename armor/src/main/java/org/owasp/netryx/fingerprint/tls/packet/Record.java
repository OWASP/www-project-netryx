package org.owasp.netryx.fingerprint.tls.packet;

import org.owasp.netryx.fingerprint.tls.packet.constant.ProtocolVersion;
import org.owasp.netryx.fingerprint.tls.packet.constant.RecordType;
import org.owasp.netryx.util.io.UIntByteArrayOutputStream;
import org.owasp.netryx.util.io.UIntInputStream;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Record
 * Represents a base TLS record packet
 */
public class Record implements RecordPacket {
    protected final RecordType recordType;

    protected ProtocolVersion tlsVersion;
    protected int nextDataLength;

    public Record(UIntInputStream in) {
        this.recordType = RecordType.of(in.readUInt8());
        this.tlsVersion = ProtocolVersion.of(in.readUInt16());
        this.nextDataLength = in.readUInt16();
    }

    public Record(RecordType recordType, ProtocolVersion tlsVersion) {
        this.recordType = recordType;
        this.tlsVersion = tlsVersion;
    }

    public void setTlsVersion(ProtocolVersion protocolVersion) {
        this.tlsVersion = protocolVersion;
    }

    public void setNextDataLength(int nextDataLength) {
        this.nextDataLength = nextDataLength;
    }

    public int getNextDataLength() {
        return nextDataLength;
    }

    public ProtocolVersion getTlsVersion() {
        return tlsVersion;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    @Override
    public Record getRecord() {
        return this;
    }

    @Override
    public byte[] toByteArray() {
        try (var out = new UIntByteArrayOutputStream()) {
            out.writeUInt8(recordType.getId());
            out.writeUInt16(tlsVersion.getId());
            out.writeUInt16(nextDataLength);

            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
