package org.owasp.netryx.mlcore.test;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.model.LogisticRegression;
import org.owasp.netryx.mlcore.optimizer.GradientDescent;
import org.owasp.netryx.mlcore.optimizer.Optimizer;
import org.owasp.netryx.mlcore.prediction.ClassificationPrediction;
import org.owasp.netryx.mlcore.regularization.L2Regularization;
import org.owasp.netryx.mlcore.regularization.Regularization;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogisticRegressionTest {

    @Test
    public void testFitAndPredict() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(2.0, 3.0, 4.0, 5.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(0.0, 0.0, 1.0, 1.0))
        ));

        var optimizer = new GradientDescent(0.1, 1000);
        var lr = LogisticRegression.newBuilder()
                .setOptimizer(optimizer)
                .setParallelism(2)
                .build();

        lr.fit(X, y);
        var predictions = lr.predict(X);

        assertEquals(0.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(0.0, predictions.get(1).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(2).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(3).getLabel(), 0.1);
    }

    @Test
    public void testFitAndPredictWithRegularization() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(2.0, 3.0, 4.0, 5.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(0.0, 0.0, 1.0, 1.0))
        ));

        var optimizer = new GradientDescent(0.1, 1000);
        var regularizer = new L2Regularization(0.1);

        var lr = LogisticRegression.newBuilder()
                .setOptimizer(optimizer)
                .setRegularizer(regularizer)
                .setParallelism(2)
                .build();

        lr.fit(X, y);
        var predictions = lr.predict(X);

        assertEquals(0.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(0.0, predictions.get(1).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(2).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(3).getLabel(), 0.1);
    }
}