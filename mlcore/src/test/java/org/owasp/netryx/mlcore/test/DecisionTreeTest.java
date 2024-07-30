package org.owasp.netryx.mlcore.test;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.model.tree.DecisionTree;
import org.owasp.netryx.mlcore.model.tree.node.TreeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DecisionTreeTest {
    private DataFrame X;
    private DataFrame y;
    private DecisionTree decisionTree;

    @BeforeEach
    public void setup() {
        X = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 1.5, 3.0)),
                "feature2", new DoubleSeries(Arrays.asList(3.0, 2.5, 2.0, 4.0))
        ));
        y = new DataFrame(Map.of(
                "target", new DoubleSeries(Arrays.asList(0.0, 1.0, 0.0, 1.0))
        ));

        decisionTree = new DecisionTree(3, 2);
        decisionTree.fit(X, y);
    }

    @Test
    public void testPredict() {
        var predictions = decisionTree.predict(X);

        assertEquals(X.height(), predictions.size());
        assertTrue(predictions.get(0).getLabel() == 0.0 || predictions.get(0).getLabel() == 1.0);
    }

    @Test
    public void testTreeConstruction() {
        assertNotNull(decisionTree);

        var root = decisionTree.getRoot();

        assertNotNull(root);
        assertTrue(root.isLeaf() || root.getLeftChild() != null);
        assertTrue(root.isLeaf() || root.getRightChild() != null);
    }

    @Test
    public void testTreeDepth() {
        var depth = decisionTree.getDepth();
        assertTrue(depth <= 3);
    }

    @Test
    public void testClassificationAccuracy() {
        var predictions = decisionTree.predict(X);
        long correct = 0;

        for (var i = 0; i < X.height(); i++) {
            if (predictions.get(i).getLabel() == (y.getColumn("target").castAs(Double.class).get(i))) {
                correct++;
            }
        }

        var accuracy = (double) correct / X.height();
        assertTrue(accuracy >= 0.5);
    }

    @Test
    public void testRegressionPrediction() {
        var X_regression = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0))
        ));
        var y_regression = new DataFrame(Map.of(
                "target", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0, 4.0))
        ));
        var regressionTree = new DecisionTree(3, 2);
        regressionTree.fit(X_regression, y_regression);

        var predictions = regressionTree.predict(X_regression);

        for (var i = 0; i < X_regression.height(); i++) {
            assertEquals(y_regression.getColumn("target").castAs(Double.class).get(i), predictions.get(i).getLabel());
        }
    }

    @Test
    public void testSpecificTreeStructure() {
        var root = getTreeNode();
        assertNotNull(root);

        assertNotNull(root.getLeftChild());
        assertNotNull(root.getRightChild());

        assertEquals(0, root.getFeatureIndex());
        assertEquals(2.5, root.getThreshold(), 0.01);

        var leftChild = root.getLeftChild();
        assertNotNull(leftChild);
        assertTrue(leftChild.isLeaf());

        var rightChild = root.getRightChild();
        assertNotNull(rightChild);
        assertTrue(rightChild.isLeaf());

        assertEquals(0.0, leftChild.getPrediction(), 0.01);
        assertEquals(1.0, rightChild.getPrediction(), 0.01);
    }

    private static TreeNode getTreeNode() {
        var X_specific = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 2.5, 3.0, 4.0)),
                "feature2", new DoubleSeries(Arrays.asList(5.0, 6.0, 7.0, 8.0, 9.0))
        ));
        var y_specific = new DataFrame(Map.of(
                "target", new DoubleSeries(Arrays.asList(0.0, 0.0, 1.0, 1.0, 1.0))
        ));
        var specificTree = new DecisionTree(3, 2);
        specificTree.fit(X_specific, y_specific);

        return specificTree.getRoot();
    }

    @Test
    public void testEdgeCases() {
        var X_empty = new DataFrame(Map.of(
                "feature1", new DoubleSeries(List.of())
        ));
        var y_empty = new DataFrame(Map.of(
                "target", new DoubleSeries(List.of())
        ));
        var emptyTree = new DecisionTree(3, 2);
        emptyTree.fit(X_empty, y_empty);

        var root = emptyTree.getRoot();
        assertNotNull(root);
        assertTrue(root.isLeaf());
    }
}