package org.owasp.netryx.mlcore.serialize.component;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogFeatureProbabilityComponent implements MLComponent {
    private Map<Double, Map<Integer, Map<Double, Double>>> logFeatureProbabilities;

    public LogFeatureProbabilityComponent(Map<Double, Map<Integer, Map<Double, Double>>> logFeatureProbabilities) {
        this.logFeatureProbabilities = logFeatureProbabilities;
    }

    public LogFeatureProbabilityComponent() {
        this(new HashMap<>());
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(logFeatureProbabilities.size());

        for (var outerEntry : logFeatureProbabilities.entrySet()) {
            out.writeDouble(outerEntry.getKey());
            var innerMap = outerEntry.getValue();
            out.writeInt(innerMap.size());

            for (var middleEntry : innerMap.entrySet()) {
                out.writeInt(middleEntry.getKey());
                var innerInnerMap = middleEntry.getValue();

                new DoubleMapComponent(innerInnerMap).save(out);
            }
        }
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        var logFeatureProbabilities = new HashMap<Double, Map<Integer, Map<Double, Double>>>();

        var outerSize = in.readInt();

        for (var i = 0; i < outerSize; i++) {
            var outerKey = in.readDouble();
            var innerSize = in.readInt();

            var innerMap = new HashMap<Integer, Map<Double, Double>>();

            for (var j = 0; j < innerSize; j++) {
                var middleKey = in.readInt();

                var component = new DoubleMapComponent();
                component.load(in);

                innerMap.put(middleKey, component.getMap());
            }

            logFeatureProbabilities.put(outerKey, innerMap);
        }

        this.logFeatureProbabilities = logFeatureProbabilities;
    }

    public Map<Double, Map<Integer, Map<Double, Double>>> getMap() {
        return logFeatureProbabilities;
    }
}
