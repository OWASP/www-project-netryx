package org.owasp.netryx.mlcore.test;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.model.tree.GradientBoosting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GradientBoostingTest {

    private DataFrame X;
    private DataFrame y;
    private GradientBoosting gradientBoosting;

    @BeforeEach
    public void setup() {
        X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(5.0, 6.0, 7.0, 8.0))
        ));
        y = new DataFrame(Map.of(
                "target", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0))
        ));

        gradientBoosting = GradientBoosting.create();
        gradientBoosting.fit(X, y);
    }

    @Test
    public void testPredict() {
        var predictions = gradientBoosting.predict(X);

        assertEquals(4, predictions.size());

        var expectedPredictions = Arrays.asList(1.0, 2.0, 3.0, 4.0);

        System.out.println("Predictions: ");
        for (var i = 0; i < predictions.size(); i++) {
            System.out.printf("Expected: %.2f, Actual: %.2f\n", expectedPredictions.get(i), predictions.get(i).getLabel());
            assertEquals(expectedPredictions.get(i), predictions.get(i).getLabel(), 0.1);
        }
    }
}