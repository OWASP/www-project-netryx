package org.owasp.netryx.mlcore.frame.series;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractSeries<T> {
    protected List<T> data;

    public AbstractSeries(List<T> data) {
        this.data = new ArrayList<>(data);
    }

    public List<T> getData() {
        return data;
    }

    public T get(int index) {
        return data.get(index);
    }

    public int size() {
        return data.size();
    }

    public <R> AbstractSeries<R> map(Function<T, R> function) {
        return createSeries(data.stream()
                .map(function)
                .collect(Collectors.toList()));
    }

    public AbstractSeries<T> fillNulls(T value) {
        List<T> filledData = new ArrayList<>();
        for (var item : data) {
            if (item == null) {
                filledData.add(value);
            } else {
                filledData.add(item);
            }
        }
        return createSeries(filledData);
    }

    public AbstractSeries<T> unique() {
        return createSeries(data.stream().distinct().collect(Collectors.toList()));
    }

    public <R> AbstractSeries<R> castAs(Class<R> clazz) {
        List<R> castedData = new ArrayList<>();
        for (var item : data) {
            if (item != null) {
                if (clazz.isInstance(item)) {
                    castedData.add(clazz.cast(item));
                } else {
                    throw new ClassCastException("Cannot cast " + item.getClass().getName() + " to " + clazz.getName());
                }
            } else {
                castedData.add(null);
            }
        }
        return createSeries(castedData);
    }

    public DoubleSeries castAsDouble() {
        return new DoubleSeries((List<Double>) data);
    }

    public IntSeries castAsInt() {
        return new IntSeries((List<Integer>) data);
    }

    public T mode() {
        if (data.isEmpty()) return null;
        Map<T, Integer> frequencyMap = new HashMap<>();
        for (var item : data) {
            frequencyMap.put(item, frequencyMap.getOrDefault(item, 0) + 1);
        }
        return Collections.max(frequencyMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public void set(int index, T value) {
        if (index < 0 || index >= data.size()) {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
        data.set(index, value);
    }

    public abstract <R> AbstractSeries<R> createSeries(List<R> data);

    @SafeVarargs
    public static <T> Series<T> of(T... data) {
        return new Series<>(new ArrayList<>(List.of(data)));
    }

    public static Series<Double> ofDouble(double... data) {
        List<Double> doubleList = new ArrayList<>();
        for (var d : data) {
            doubleList.add(d);
        }
        return new DoubleSeries(doubleList);
    }

    public static Series<Integer> ofInt(int... data) {
        List<Integer> intList = new ArrayList<>();
        for (var i : data) {
            intList.add(i);
        }
        return new IntSeries(intList);
    }

    public AbstractSeries<T> copy() {
        return createSeries(new ArrayList<>(data));
    }
}