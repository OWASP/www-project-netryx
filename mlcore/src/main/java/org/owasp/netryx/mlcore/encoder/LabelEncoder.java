package org.owasp.netryx.mlcore.encoder;

import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.serialize.component.StringDoubleMapComponent;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class LabelEncoder implements Encoder {
    private String columnName;
    private Map<String, Double> labelMapping;

    @Override
    public void fit(DataFrame df, String columnName) {
        this.columnName = columnName;
        this.labelMapping = new HashMap<>();

        var values = (df.getColumn(columnName).castAs(String.class))
                .unique()
                .getData();

        var label = 0.0;

        for (var value : values)
            labelMapping.put(value, label++);
    }

    @Override
    public DataFrame transform(DataFrame df) {
        var originalSeries = df.getColumn(columnName).castAs(String.class);
        List<Double> transformedData = new ArrayList<>();

        for (var value : originalSeries.getData()) {
            transformedData.add(labelMapping.get(value));
        }

        var transformedSeries = new DoubleSeries(transformedData);
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>(df.getData());
        newData.put(columnName, transformedSeries);

        return new DataFrame(newData);
    }

    public Map<String, Double> getLabelMapping() {
        return labelMapping;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_ENCODER);

        out.writeUTF(columnName);
        new StringDoubleMapComponent(labelMapping).save(out);

        out.writeInt(MLFlag.END_ENCODER);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartEncoder(in.readInt());

        this.columnName = in.readUTF();

        var component = new StringDoubleMapComponent();
        component.load(in);

        this.labelMapping = component.getMap();

        MLFlag.ensureEndEncoder(in.readInt());
    }
}