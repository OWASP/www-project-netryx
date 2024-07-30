package org.owasp.netryx.mlcore.frame.series;

import java.util.*;

public class Series<T> extends AbstractSeries<T> {
    public Series(List<T> data) {
        super(data);
    }

    @Override
    protected <R> AbstractSeries<R> createSeries(List<R> data) {
        return new Series<>(data);
    }
}