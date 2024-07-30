package org.owasp.netryx.mlcore.test;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.model.LinearRegression;
import org.owasp.netryx.mlcore.regularization.L2Regularization;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LinearRegressionTest {

    @Test
    public void testFitAndPredictWithoutRegularization() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(2.0, 3.0, 4.0, 5.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(3.0, 5.0, 7.0, 9.0))
        ));

        var lr = LinearRegression.create();
        lr.fit(X, y);

        var predictions = lr.predict(X);

        assertEquals(3.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(5.0, predictions.get(1).getLabel(), 0.1);
        assertEquals(7.0, predictions.get(2).getLabel(), 0.1);
        assertEquals(9.0, predictions.get(3).getLabel(), 0.1);
    }

    @Test
    public void testFitAndPredictWithRegularization() {
        var regularizer = new L2Regularization(0.01);

        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(2.0, 3.0, 4.0, 5.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(3.0, 5.0, 7.0, 9.0))
        ));

        var lr = LinearRegression.create(regularizer);
        lr.fit(X, y);

        var predictions = lr.predict(X);

        assertEquals(3.0, predictions.get(0).getLabel(), 0.1);
        assertEquals(5.0, predictions.get(1).getLabel(), 0.1);
        assertEquals(7.0, predictions.get(2).getLabel(), 0.1);
        assertEquals(9.0, predictions.get(3).getLabel(), 0.1);
    }

    @Test
    public void testGetCoefficients() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(2.0, 3.0, 4.0, 5.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(3.0, 5.0, 7.0, 9.0))
        ));

        var lr = LinearRegression.create();
        lr.fit(X, y);

        var coefficients = lr.getCoefficients();

        assertNotNull(coefficients);
        assertEquals(3, coefficients.getNumRows());
        assertEquals(1, coefficients.getNumCols());
    }

    @Test
    public void testSingularMatrixException() {
        var X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 1.0, 1.0, 1.0)),
                "feature2", new DoubleSeries(Arrays.asList(2.0, 2.0, 2.0, 2.0))
        ));
        var y = new DataFrame(Map.of(
                "label", new DoubleSeries(Arrays.asList(3.0, 3.0, 3.0, 3.0))
        ));

        var lr = LinearRegression.create();

        assertThrows(IllegalArgumentException.class, () -> lr.fit(X, y));
    }

    @Test
    public void testHyperParameters() {
        var regularizer = new L2Regularization(0.1);

        var lr = LinearRegression.create(regularizer);

        var hyperParameters = lr.getHyperParameters();

        assertFalse(hyperParameters.isEmpty());
        assertEquals("lambda", hyperParameters.get(0).getName());
        assertEquals(0.1, hyperParameters.get(0).getValue());
    }
}