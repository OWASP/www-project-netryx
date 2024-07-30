package org.owasp.netryx.mlcore.encoder;

import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.Series;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class OneHotEncoder implements Encoder {
    private String columnName;
    private Set<String> uniqueValues;

    @Override
    public void fit(DataFrame df, String columnName) {
        this.columnName = columnName;

        this.uniqueValues = new HashSet<>(df.getColumn(columnName)
                .castAs(String.class)
                .unique()
                .getData());
    }

    @Override
    public DataFrame transform(DataFrame df) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>(df.getData());
        newData.remove(columnName);

        for (var uniqueValue : uniqueValues) {
            var encodedColumn = new double[df.height()];

            for (var i = 0; i < df.height(); i++) {
                var value = df.getColumn(columnName).castAs(String.class).get(i);
                encodedColumn[i] = value.equals(uniqueValue) ? 1.0 : 0.0;
            }

            newData.put(columnName + "_" + uniqueValue, Series.ofDouble(encodedColumn));
        }

        return new DataFrame(newData);
    }

    public String getColumnName() {
        return columnName;
    }

    public Set<String> getUniqueValues() {
        return uniqueValues;
    }
}