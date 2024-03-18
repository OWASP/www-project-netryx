package org.owasp.netryx.fingerprint.http2;

public class PriorityFrame implements Comparable<PriorityFrame> {
    private static final String FORMAT = "%s:%s:%s:%s";

    private final int streamId;
    private final byte exclusiveBit;
    private final int dependentStreamId;
    private final short weight;

    public PriorityFrame(int streamId, byte exclusiveBit, int dependentStreamId, short weight) {
        this.streamId = streamId;
        this.exclusiveBit = exclusiveBit;
        this.dependentStreamId = dependentStreamId;
        this.weight = weight;
    }

    public long getStreamId() {
        return streamId;
    }

    public byte getExclusiveBit() {
        return exclusiveBit;
    }

    public int getDependentStreamId() {
        return dependentStreamId;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format(FORMAT, streamId, exclusiveBit, dependentStreamId, weight);
    }

    @Override
    public int compareTo(PriorityFrame o) {
        return Long.compare(streamId, o.streamId);
    }
}
