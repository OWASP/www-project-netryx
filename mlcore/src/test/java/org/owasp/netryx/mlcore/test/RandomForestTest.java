package org.owasp.netryx.mlcore.test;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.model.tree.RandomForest;
import org.owasp.netryx.mlcore.model.tree.node.TreeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RandomForestTest {
    private DataFrame X;
    private DataFrame y;
    private RandomForest randomForest;

    @BeforeEach
    public void setup() {
        X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 1.5, 3.0)),
                "feature2", new DoubleSeries(Arrays.asList(3.0, 2.5, 2.0, 4.0))
        ));
        y = new DataFrame(Map.of(
                "target", new DoubleSeries(Arrays.asList(0.0, 1.0, 0.0, 1.0))
        ));

        randomForest = RandomForest.newBuilder()
                .setRandomState(42L)
                .build();

        randomForest.fit(X, y);
    }

    @Test
    public void testRegressionPrediction() {
        var X_regression = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0))
        ));
        var y_regression = new DataFrame(Map.of(
                "target", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0))
        ));
        var regressionForest = RandomForest.newBuilder()
                .setRandomState(42L)
                .build();

        regressionForest.fit(X_regression, y_regression);

        var predictions = regressionForest.predict(X_regression);

        for (var i = 0; i < X_regression.height(); i++) {
            assertEquals(y_regression.getColumn("target").castAs(Double.class).get(i), predictions.get(i).getLabel(), 0.5);
        }
    }

    @Test
    public void testSpecificTreeStructure() {
        var X_specific = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 2.5, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(5.0, 6.0, 7.0, 8.0, 9.0))
        ));
        var y_specific = new DataFrame(Map.of(
                "target", new DoubleSeries(Arrays.asList(0.0, 0.0, 1.0, 1.0, 1.0))
        ));
        var specificForest = RandomForest.newBuilder()
                .setRandomState(42L)
                .build();

        specificForest.fit(X_specific, y_specific);

        var root = specificForest.getTrees().get(0).getRoot();
        assertNotNull(root);

        assertNotNull(root.getLeftChild());
        assertNotNull(root.getRightChild());

        assertEquals(0, root.getFeatureIndex()); // feature1 имеет индекс 0
        assertEquals(2.5, root.getThreshold(), 0.5);

        var leftChild = root.getLeftChild();
        assertNotNull(leftChild);
        assertTrue(leftChild.isLeaf());

        var rightChild = root.getRightChild();
        assertNotNull(rightChild);
        assertTrue(rightChild.isLeaf());

        assertEquals(0.0, leftChild.getPrediction(), 0.01);
        assertEquals(1.0, rightChild.getPrediction(), 0.01);
    }

    @Test
    public void testClassificationAccuracy() {
        var predictions = randomForest.predict(X);
        var expectedPredictions = Arrays.asList(0.0, 1.0, 0.0, 1.0);
        for (var i = 0; i < X.height(); i++) {
            assertEquals(expectedPredictions.get(i), predictions.get(i).getLabel(), 0.1);
        }
    }
}