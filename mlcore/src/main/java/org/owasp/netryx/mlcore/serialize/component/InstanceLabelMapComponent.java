package org.owasp.netryx.mlcore.serialize.component;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InstanceLabelMapComponent implements MLComponent {
    private Map<double[], Double> map;

    public InstanceLabelMapComponent(Map<double[], Double> map) {
        this.map = map;
    }

    public InstanceLabelMapComponent() {
        this(new HashMap<>());
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(map.size());

        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            out.writeInt(key.length);

            for (var d : key)
                out.writeDouble(d);

            out.writeDouble(value);
        }
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        var size = in.readInt();
        var instanceLabelMap = new HashMap<double[], Double>();

        for (var i = 0; i < size; i++) {
            var keyLength = in.readInt();
            var key = new double[keyLength];

            for (var j = 0; j < keyLength; j++)
                key[j] = in.readDouble();

            var value = in.readDouble();
            instanceLabelMap.put(key, value);
        }

        this.map = instanceLabelMap;
    }

    public Map<double[], Double> getMap() {
        return map;
    }
}
