package org.owasp.netryx.mlcore.metrics;

import org.owasp.netryx.mlcore.Model;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.prediction.Prediction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ModelEvaluator {
    private final Model model;

    public ModelEvaluator(Model model) {
        this.model = model;
    }

    public EvaluationMetrics evaluate(DataFrame testX, DataFrame testY) {
        var predictions = predict(testX);
        var ySeries = testY.getColumn(0).castAsDouble();

        var tp = initializeMap(ySeries);
        var fp = initializeMap(ySeries);
        var fn = initializeMap(ySeries);
        var tn = initializeMap(ySeries);

        prepareConfusionMatrix(ySeries, predictions, tp, fp, fn, tn);

        var precision = calculatePrecision(tp, fp);
        var recall = calculateRecall(tp, fn);
        var accuracy = calculateAccuracy(tp, tn, fp, fn);
        var f1Score = calculateF1Score(precision, recall);

        return new EvaluationMetrics(accuracy, precision, recall, f1Score);
    }

    private List<? extends Prediction> predict(DataFrame X) {
        return model.predict(X);
    }

    private Map<Double, AtomicInteger> initializeMap(DoubleSeries ySeries) {
        Map<Double, AtomicInteger> map = new HashMap<>();
        for (double label : ySeries.unique().getData()) {
            map.put(label, new AtomicInteger(0));
        }
        return map;
    }

    private void prepareConfusionMatrix(
            DoubleSeries ySeries, List<? extends Prediction> predictions,
            Map<Double, AtomicInteger> tp, Map<Double, AtomicInteger> fp,
            Map<Double, AtomicInteger> fn, Map<Double, AtomicInteger> tn
    ) {
        for (var i = 0; i < ySeries.size(); i++) {
            var trueLabel = ySeries.getDouble(i);
            var predictedLabel = predictions.get(i).getLabel();

            for (double label : ySeries.unique().getData()) {
                if (label == trueLabel && label == predictedLabel) {
                    tp.get(label).incrementAndGet();
                } else if (label != trueLabel && label == predictedLabel) {
                    fp.get(label).incrementAndGet();
                } else if (label == trueLabel && label != predictedLabel) {
                    fn.get(label).incrementAndGet();
                } else {
                    tn.get(label).incrementAndGet();
                }
            }
        }
    }

    private double calculatePrecision(Map<Double, AtomicInteger> tp, Map<Double, AtomicInteger> fp) {
        return tp.entrySet().stream()
                .mapToDouble(entry -> {
                    double label = entry.getKey();
                    return (double) entry.getValue().get() / (entry.getValue().get() + fp.get(label).get());
                })
                .average()
                .orElse(0.0);
    }

    private double calculateRecall(Map<Double, AtomicInteger> tp, Map<Double, AtomicInteger> fn) {
        return tp.entrySet().stream()
                .mapToDouble(entry -> {
                    double label = entry.getKey();
                    return (double) entry.getValue().get() / (entry.getValue().get() + fn.get(label).get());
                })
                .average()
                .orElse(0.0);
    }

    private double calculateAccuracy(
            Map<Double, AtomicInteger> tp, Map<Double, AtomicInteger> tn,
            Map<Double, AtomicInteger> fp, Map<Double, AtomicInteger> fn
    ) {
        return tp.entrySet().stream()
                .mapToDouble(entry -> {
                    double label = entry.getKey();
                    return (double) (entry.getValue().get() + tn.get(label).get()) /
                            (entry.getValue().get() + tn.get(label).get() + fp.get(label).get() + fn.get(label).get());
                })
                .average()
                .orElse(0.0);
    }

    private double calculateF1Score(double precision, double recall) {
        return 2 * (precision * recall) / (precision + recall);
    }
}