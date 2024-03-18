package org.owasp.netryx.fingerprint.http2;

import java.util.*;
import java.util.stream.Collectors;

public class AkamaiHttp2Fingerprint {
    private final Map<Integer, Long> settings = new TreeMap<>(Comparator.naturalOrder());
    private int windowUpdateValue = 0;
    private final Set<PriorityFrame> priorityFrames = new TreeSet<>();
    private final List<String> orderedPseudoHeaders = new ArrayList<>();

    public Map<Integer, Long> getSettings() {
        return settings;
    }

    public long getWindowUpdateValue() {
        return windowUpdateValue;
    }

    public Set<PriorityFrame> getPriorityFrames() {
        return priorityFrames;
    }

    public void addSettings(int id, long value) {
        settings.put(id, value);
    }

    public void addPseudoHeader(String header) {
        if (!orderedPseudoHeaders.contains(header))
            orderedPseudoHeaders.add(header);
    }

    public void setWindowUpdateValue(int windowUpdateValue) {
        this.windowUpdateValue = windowUpdateValue;
    }

    public void addPriorityFrame(PriorityFrame frame) {
        priorityFrames.add(frame);
    }

    @Override
    public String toString() {
        var fingerprint = new StringJoiner("|");

        var settingsPart = new StringJoiner(";");
        settings.forEach((key, value) -> settingsPart.add(String.format("%s:%s", key, value)));

        var framesPart = new StringJoiner(",");

        if (priorityFrames.isEmpty())
            framesPart.add("0");
        else
            priorityFrames.forEach(frame -> framesPart.add(frame.toString()));

        var headerOrder = orderedPseudoHeaders.stream().map(s -> String.valueOf(s.charAt(1)))
                        .collect(Collectors.joining(","));

        fingerprint.add(settingsPart.toString());
        fingerprint.add(String.valueOf(windowUpdateValue));
        fingerprint.add(framesPart.toString());
        fingerprint.add(headerOrder);

        return fingerprint.toString();
    }
}
