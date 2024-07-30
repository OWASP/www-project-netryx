package org.owasp.netryx.mlcore.test;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.model.knn.KNNClassifier;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KNNClassifierTest {

    @Test
    public void testFitAndPredictSimpleData() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 1.0, 2.0, 2.0)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 2.0, 1.0, 2.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(0.0, 0.0, 1.0, 1.0))
        ));

        var knn = KNNClassifier.create(1);
        knn.fit(X, y);

        var predictions = knn.predict(X);

        assertEquals(0.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(0.0, predictions.get(1).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(2).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(3).getLabel(), 0.1);
    }

    @Test
    public void testPredictWithK3() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 1.0, 2.0, 2.0, 3.0)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 2.0, 1.0, 2.0, 1.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(0.0, 0.0, 1.0, 1.0, 1.0))
        ));

        var knn = KNNClassifier.create(3);
        knn.fit(X, y);

        var predictions = knn.predict(X);

        assertEquals(0.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(0.0, predictions.get(1).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(2).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(3).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(4).getLabel(), 0.1);
    }

    @Test
    public void testPredictWithDifferentData() {
        var X_train = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 1.0, 2.0, 2.0))
        ));
        var y_train = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(0.0, 0.0, 1.0, 1.0))
        ));
        var X_test = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.5, 3.5)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 2.0))
        ));

        var knn = KNNClassifier.create(1);
        knn.fit(X_train, y_train);

        var predictions = knn.predict(X_test);

        assertEquals(0.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(1).getLabel(), 0.1);
    }

    @Test
    public void testPredictWithDifferentK() {
        var X_train = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 1.0, 2.0, 2.0))
        ));
        var y_train = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(0.0, 0.0, 1.0, 1.0))
        ));
        var X_test = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.5, 3.5)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 2.0))
        ));

        var knn = KNNClassifier.create(3);
        knn.fit(X_train, y_train);

        var predictions = knn.predict(X_test);

        assertEquals(0.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(1).getLabel(), 0.1);
    }

    @Test
    public void testHyperParameterK() {
        var knn = KNNClassifier.create(5);
        assertEquals(5, knn.getHyperParameters().get(0).getValue());
    }
}
