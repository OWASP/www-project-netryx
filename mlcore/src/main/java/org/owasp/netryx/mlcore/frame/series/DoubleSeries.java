package org.owasp.netryx.mlcore.frame.series;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DoubleSeries extends Series<Double> implements NumericSeries {
    public DoubleSeries(List<Double> data) {
        super(data);
    }

    public double getDouble(int index) {
        return data.get(index);
    }

    public DoubleSeries mapDouble(Function<Double, Double> function) {
        var mappedData = data.stream()
                .map(function)
                .collect(Collectors.toList());

        return new DoubleSeries(new ArrayList<>(mappedData));
    }

    @Override
    public <R> AbstractSeries<R> createSeries(List<R> data) {
        return (AbstractSeries<R>) super.createSeries(data).castAsDouble();
    }

    @Override
    public double mean() {
        if (data.isEmpty()) return 0;

        var sum = 0.0;

        for (var item : data) {
            sum += item;
        }
        return sum / data.size();
    }

    @Override
    public double sum() {
        if (data.isEmpty()) return 0;

        var sum = 0.0;

        for (var item : data)
            sum += item;

        return sum;
    }

    @Override
    public AbstractSeries<Double> copy() {
        return new DoubleSeries(new ArrayList<>(data));
    }
}