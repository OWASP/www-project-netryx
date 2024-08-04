package org.owasp.netryx.mlcore.serialize.component;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StringDoubleMapComponent implements MLComponent {
    private Map<String, Double> map;

    public StringDoubleMapComponent(Map<String, Double> map) {
        this.map = map;
    }

    public StringDoubleMapComponent() {
        this(new HashMap<>());
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(map.size());

        for (var entry : map.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeDouble(entry.getValue());
        }
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        var size = in.readInt();

        map = new HashMap<>(size);

        for (var i = 0; i < size; i++) {
            var key = in.readUTF();
            var value = in.readDouble();

            map.put(key, value);
        }
    }

    public Map<String, Double> getMap() {
        return map;
    }
}
