package org.owasp.netryx.mlcore.frame;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.frame.series.IntSeries;
import org.owasp.netryx.mlcore.frame.series.Series;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DataFrame {
    private final Map<String, AbstractSeries<?>> data;
    private final List<String> columns;

    public DataFrame(Map<String, AbstractSeries<?>> data) {
        this.data = data;
        this.columns = new ArrayList<>(data.keySet());
    }

    public AbstractSeries<?> getColumn(String name) {
        return data.get(name);
    }

    public AbstractSeries<?> getColumn(int index) {
        return data.get(columns.get(index));
    }

    public int width() {
        return columns.size();
    }

    public int height() {
        if (columns.isEmpty()) {
            return 0;
        }
        return data.get(columns.get(0)).size();
    }

    public DataFrame select(String... columnNames) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>();
        for (var col : columnNames) {
            newData.put(col, data.get(col));
        }
        return new DataFrame(newData);
    }

    public DataFrame filterByIndex(IntPredicate predicate) {
        List<Integer> validIndices = new ArrayList<>();
        var numRows = data.values().iterator().next().size();

        for (var i = 0; i < numRows; i++) {
            if (predicate.test(i)) {
                validIndices.add(i);
            }
        }

        return selectRows(validIndices);
    }

    public DataFrame selectRows(List<Integer> rowIndices) {
        return getDataFrame(rowIndices);
    }

    private DataFrame getDataFrame(List<Integer> rowIndices) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>();

        for (var col : columns) {
            List<Object> selectedData = rowIndices.stream().map(idx -> data.get(col).get(idx)).collect(Collectors.toList());
            newData.put(col, new Series<>(selectedData));
        }

        return new DataFrame(newData);
    }

    public DataFrame filter(Predicate<Map<String, Object>> predicate) {
        List<Integer> validIndices = new ArrayList<>();
        var numRows = data.values().iterator().next().size();

        for (var i = 0; i < numRows; i++) {
            Map<String, Object> row = new HashMap<>();
            for (var col : columns) {
                row.put(col, data.get(col).get(i));
            }
            if (predicate.test(row)) {
                validIndices.add(i);
            }
        }

        return getDataFrame(validIndices);
    }

    public DataFrame head(int n) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>();
        for (var col : columns) {
            var headData = data.get(col).getData().subList(0, Math.min(n, data.get(col).size()));
            newData.put(col, new Series<>(headData));
        }
        return new DataFrame(newData);
    }

    public DataFrame tail(int n) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>();
        for (var col : columns) {
            var tailData = data.get(col).getData().subList(Math.max(0, data.get(col).size() - n), data.get(col).size());
            newData.put(col, new Series<>(tailData));
        }
        return new DataFrame(newData);
    }

    public <T> DataFrame fillNulls(String columnName, T value) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>(data);

        var series = (Series<T>) data.get(columnName);

        newData.put(columnName, series.fillNulls(value));
        return new DataFrame(newData);
    }

    public SimpleMatrix toSimpleMatrix() {
        return toSimpleMatrix(0, height());
    }

    public SimpleMatrix toSimpleMatrix(int start, int end) {
        var numRows = end - start;
        var numCols = width();
        var matrix = new SimpleMatrix(numRows, numCols);

        for (var i = 0; i < numCols; i++) {
            var column = getColumn(i);

            if (column instanceof DoubleSeries) {
                var doubleSeries = (DoubleSeries) column;
                for (var j = start; j < end; j++) {
                    matrix.set(j - start, i, doubleSeries.getDouble(j));
                }
            } else if (column instanceof IntSeries) {
                var intSeries = (IntSeries) column;
                for (var j = start; j < end; j++) {
                    matrix.set(j - start, i, intSeries.getInt(j));
                }
            } else {
                throw new IllegalArgumentException("Unsupported column type: " + column.getClass());
            }
        }

        return matrix;
    }

    public DataFrame slice(int start, int end) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>();
        for (var col : columns) {
            var slicedData = data.get(col).getData().subList(start, end);
            newData.put(col, new Series<>(slicedData));
        }
        return new DataFrame(newData);
    }

    public static DataFrame concat(List<DataFrame> dataFrames) {
        if (dataFrames.isEmpty()) {
            throw new IllegalArgumentException("No DataFrames to concatenate");
        }

        Map<String, List<Object>> concatenatedData = new LinkedHashMap<>();
        var columns = dataFrames.get(0).getColumns();

        for (var column : columns) {
            List<Object> concatenatedColumn = new ArrayList<>();
            for (var df : dataFrames) {
                concatenatedColumn.addAll(df.getColumn(column).getData());
            }
            concatenatedData.put(column, concatenatedColumn);
        }

        Map<String, AbstractSeries<?>> finalData = new LinkedHashMap<>();
        for (var column : columns) {
            finalData.put(column, new Series<>(concatenatedData.get(column)));
        }

        return new DataFrame(finalData);
    }

    public Map<String, AbstractSeries<?>> getData() {
        return data;
    }

    public List<String> getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(String.join("\t", columns)).append("\n");
        var numRows = data.values().iterator().next().size();

        for (var i = 0; i < numRows; i++) {
            for (var col : columns) {
                sb.append(data.get(col).get(i)).append("\t");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}