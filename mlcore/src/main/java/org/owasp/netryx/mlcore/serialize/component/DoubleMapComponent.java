package org.owasp.netryx.mlcore.serialize.component;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DoubleMapComponent implements MLComponent {
    private Map<Double, Double> map;

    public DoubleMapComponent(Map<Double, Double> map) {
        this.map = map;
    }

    public DoubleMapComponent() {
        this(new HashMap<>());
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(map.size());

        for (var entry : map.entrySet()) {
            out.writeDouble(entry.getKey());
            out.writeDouble(entry.getValue());
        }
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        var size = in.readInt();
        var map = new HashMap<Double, Double>();

        for (var i = 0; i < size; i++) {
            var key = in.readDouble();
            var value = in.readDouble();

            map.put(key, value);
        }

        this.map = map;
    }

    public Map<Double, Double> getMap() {
        return map;
    }
}
