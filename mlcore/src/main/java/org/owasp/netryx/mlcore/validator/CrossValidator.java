package org.owasp.netryx.mlcore.validator;

import org.owasp.netryx.mlcore.Model;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.metrics.EvaluationMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrossValidator {
    private final int k;

    public CrossValidator(int k) {
        this.k = k;
    }

    public List<DataFrame[]> split(DataFrame df) {
        var numRows = df.height();
        var foldSize = numRows / k;

        List<DataFrame> folds = new ArrayList<>();

        for (var i = 0; i < k; i++)
            folds.add(df.slice(i * foldSize, (i + 1) * foldSize));

        List<DataFrame[]> splits = new ArrayList<>();
        for (var i = 0; i < k; i++) {
            var testFold = folds.get(i);
            var trainFold = DataFrame.concat(folds.stream().filter(f -> f != testFold)
                    .collect(Collectors.toList()));

            splits.add(new DataFrame[]{trainFold, testFold});
        }

        return splits;
    }

    public EvaluationMetrics crossValidate(Model model, DataFrame X, DataFrame y) {
        var splits = split(X);
        var targetSplits = split(y);

        List<EvaluationMetrics> metricsList = new ArrayList<>();

        for (var i = 0; i < k; i++) {
            var trainX = splits.get(i)[0];
            var testX = splits.get(i)[1];
            var trainY = targetSplits.get(i)[0];
            var testY = targetSplits.get(i)[1];

            model.fit(trainX, trainY);
            var metrics = model.evaluator().evaluate(testX, testY);
            metricsList.add(metrics);
        }

        return averageMetrics(metricsList);
    }

    private EvaluationMetrics averageMetrics(List<EvaluationMetrics> metricsList) {
        var accuracy = metricsList.stream().mapToDouble(EvaluationMetrics::getAccuracy).average().orElse(0.0);
        var precision = metricsList.stream().mapToDouble(EvaluationMetrics::getPrecision).average().orElse(0.0);
        var recall = metricsList.stream().mapToDouble(EvaluationMetrics::getRecall).average().orElse(0.0);
        var f1Score = metricsList.stream().mapToDouble(EvaluationMetrics::getF1Score).average().orElse(0.0);

        return new EvaluationMetrics(accuracy, precision, recall, f1Score);
    }
}