package org.owasp.netryx.mlcore.encoder;

import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;

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
}
