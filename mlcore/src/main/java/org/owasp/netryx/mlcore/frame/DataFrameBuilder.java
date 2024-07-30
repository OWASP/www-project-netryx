package org.owasp.netryx.mlcore.frame;

import org.owasp.netryx.mlcore.frame.loader.DataFrameLoader;
import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.series.Series;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataFrameBuilder {
    private DataFrameLoader loader;
    private final Map<String, AbstractSeries<?>> data = new LinkedHashMap<>();

    public DataFrameBuilder() {
    }

    public DataFrameBuilder(DataFrameLoader loader) {
        this.loader = loader;
    }

    public DataFrameBuilder fromFile(String filePath) throws IOException {
        var df = loader.load(filePath);
        addData(df.getData());
        return this;
    }

    public DataFrameBuilder addColumn(String columnName, Series<?> series) {
        data.put(columnName, series);
        return this;
    }

    public DataFrameBuilder addData(Map<String, AbstractSeries<?>> data) {
        this.data.putAll(data);
        return this;
    }

    public DataFrame build() {
        return new DataFrame(data);
    }

    public static DataFrame fromMap(Map<String, AbstractSeries<?>> data) {
        return new DataFrame(data);
    }
}