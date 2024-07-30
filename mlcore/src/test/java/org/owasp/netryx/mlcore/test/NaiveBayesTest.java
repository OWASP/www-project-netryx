package org.owasp.netryx.mlcore.test;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.model.NaiveBayes;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NaiveBayesTest {
    
    @Test
    public void testNaiveBayesSimpleData() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 1.0, 2.0, 2.0)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 2.0, 1.0, 2.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(1.0, 1.0, -1.0, -1.0))
        ));

        var nb = NaiveBayes.create();
        nb.fit(X, y);

        var predictions = nb.predict(X);

        assertEquals(1.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(1).getLabel(), 0.1);
        assertEquals(-1.0, predictions.get(2).getLabel(), 0.1);
        assertEquals(-1.0, predictions.get(3).getLabel(), 0.1);
    }

    @Test
    public void testNaiveBayesWithAlpha() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 2.0, 1.0, 2.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(-1.0, -1.0, 1.0, 1.0))
        ));

        var nb = NaiveBayes.newBuilder()
                .setAlpha(0.5)
                .setParallelism(2)
                .build();
        nb.fit(X, y);

        var predictions = nb.predict(X);

        predictions.forEach(pred -> System.out.println(pred.getLabel()));

        assertEquals(-1.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(-1.0, predictions.get(1).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(2).getLabel(), 0.1);
        assertEquals(1.0, predictions.get(3).getLabel(), 0.1);
    }

    @Test
    public void testNaiveBayesInvalidAlpha() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0)),
                "feature2", new DoubleSeries(Arrays.asList(1.0, 2.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(-1.0, 1.0))
        ));

        assertThrows(IllegalArgumentException.class, () -> NaiveBayes.newBuilder()
                .setAlpha(-1.0)
                .setParallelism(2)
                .build()
        );
    }
}