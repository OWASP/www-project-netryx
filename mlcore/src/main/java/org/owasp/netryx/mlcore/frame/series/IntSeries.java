package org.owasp.netryx.mlcore.frame.series;

import java.util.ArrayList;
import java.util.List;

public class IntSeries extends Series<Integer> implements NumericSeries {
    public IntSeries(List<Integer> data) {
        super(data);
    }

    public int getInt(int index) {
        return data.get(index);
    }

    @Override
    public double mean() {
        if (data.isEmpty()) return 0;

        var sum = 0.0;

        for (var item : data)
            sum += item;

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
    public <R> AbstractSeries<R> createSeries(List<R> data) {
        return (AbstractSeries<R>) super.createSeries(data).castAsInt();
    }


    @Override
    public AbstractSeries<Integer> copy() {
        return new IntSeries(new ArrayList<>(data));
    }
}