package org.owasp.netryx.mlcore.model.tree;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.Regressor;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.model.tree.node.TreeNode;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.prediction.LabelPrediction;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DecisionTree implements Regressor {
    public static final String HYPER_PARAM_MAX_DEPTH = "maxDepth";
    public static final String HYPER_PARAM_MIN_SAMPLES_SPLIT = "minSamplesSplit";

    private final IntegerHyperParameter maxDepth;
    private final IntegerHyperParameter minSamplesSplit;

    private TreeNode root = new TreeNode();

    public DecisionTree(int maxDepth, int minSamplesSplit) {
        this.maxDepth = new IntegerHyperParameter(maxDepth, HYPER_PARAM_MAX_DEPTH);
        this.minSamplesSplit = new IntegerHyperParameter(minSamplesSplit, HYPER_PARAM_MIN_SAMPLES_SPLIT);
    }

    @Override
    public void fit(DataFrame X, DataFrame y) {
        if (X == null || y == null) {
            throw new IllegalArgumentException("Input DataFrames cannot be null");
        }
        root = buildTree(X, y, 0);
    }

    @Override
    public List<LabelPrediction> predict(DataFrame X) {
        if (X == null) {
            throw new IllegalArgumentException("Input DataFrame cannot be null");
        }
        var predictions = new ArrayList<LabelPrediction>();

        for (var i = 0; i < X.height(); i++) {
            var row = X.toSimpleMatrix().extractVector(true, i);
            predictions.add(predictRegression(row));
        }

        return predictions;
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return List.of(maxDepth, minSamplesSplit);
    }

    public TreeNode getRoot() {
        return root;
    }

    public int getDepth() {
        return calculateDepth(root);
    }

    private int calculateDepth(TreeNode node) {
        if (node == null || node.isLeaf()) {
            return 0;
        }
        return 1 + Math.max(calculateDepth(node.getLeftChild()), calculateDepth(node.getRightChild()));
    }

    private TreeNode buildTree(DataFrame X, DataFrame y, int depth) {
        if (depth >= maxDepth.getValue() || X.height() < minSamplesSplit.getValue()) {
            return createLeafNode(y);
        }

        var bestFeature = -1;
        var bestThreshold = Double.NaN;
        var bestGain = -Double.MAX_VALUE;

        for (var featureIndex = 0; featureIndex < X.width(); featureIndex++) {
            var thresholds = findUniqueValues(X, featureIndex);

            for (var threshold : thresholds) {
                var gain = calculateGain(X, y, featureIndex, threshold);

                if (gain > bestGain || (gain == bestGain && threshold < bestThreshold)) {
                    bestFeature = featureIndex;
                    bestThreshold = threshold;
                    bestGain = gain;
                }
            }
        }

        if (bestGain == 0) {
            return createLeafNode(y);
        }

        var splits = splitData(X, y, bestFeature, bestThreshold);
        var node = new TreeNode(bestFeature, bestThreshold);
        node.setLeftChild(buildTree(splits[0], splits[2], depth + 1));
        node.setRightChild(buildTree(splits[1], splits[3], depth + 1));

        return node;
    }

    private double[] findUniqueValues(DataFrame X, int featureIndex) {
        return X.getColumn(featureIndex).castAs(Double.class).getData()
                .stream().filter(Objects::nonNull).distinct().mapToDouble(Double::doubleValue).toArray();
    }

    private double calculateGain(DataFrame X, DataFrame y, int featureIndex, double threshold) {
        var splits = splitData(X, y, featureIndex, threshold);
        var leftY = splits[2];
        var rightY = splits[3];

        var initialImpurity = calculateImpurity(y);
        var leftImpurity = calculateImpurity(leftY);
        var rightImpurity = calculateImpurity(rightY);

        var leftWeight = (double) leftY.height() / y.height();
        var rightWeight = (double) rightY.height() / y.height();

        return initialImpurity - (leftWeight * leftImpurity + rightWeight * rightImpurity);
    }

    private double calculateImpurity(DataFrame y) {
        var ySeries = y.getColumn(0).castAsDouble();
        var counts = ySeries.getData().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        var impurity = 0.0;
        for (long count : counts.values()) {
            var probability = (double) count / y.height();
            impurity += probability * (1 - probability);
        }
        return impurity;
    }

    private DataFrame[] splitData(DataFrame X, DataFrame y, int featureIndex, double threshold) {
        List<Integer> leftIndices = new ArrayList<>();
        List<Integer> rightIndices = new ArrayList<>();

        for (var i = 0; i < X.height(); i++) {
            var value = X.getColumn(featureIndex).castAs(Double.class).get(i);
            if (value == null) continue;

            if (value < threshold) {
                leftIndices.add(i);
            } else {
                rightIndices.add(i);
            }
        }

        var leftX = X.selectRows(leftIndices);
        var rightX = X.selectRows(rightIndices);
        var leftY = y.selectRows(leftIndices);
        var rightY = y.selectRows(rightIndices);

        return new DataFrame[]{leftX, rightX, leftY, rightY};
    }

    private TreeNode createLeafNode(DataFrame y) {
        var leaf = new TreeNode();
        var series = y.getColumn(0).castAsDouble();
        leaf.setPrediction(series.mean());
        return leaf;
    }

    private LabelPrediction predictRegression(SimpleMatrix row) {
        var node = root;
        while (!node.isLeaf()) {
            if (row.get(node.getFeatureIndex()) < node.getThreshold()) {
                node = node.getLeftChild();
            } else {
                node = node.getRightChild();
            }
        }
        return new LabelPrediction(node.getPrediction());
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_MODEL);

        maxDepth.save(out);
        minSamplesSplit.save(out);
        root.save(out);

        out.writeInt(MLFlag.END_MODEL);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartModel(in.readInt());

        maxDepth.load(in);
        minSamplesSplit.load(in);
        root.load(in);

        MLFlag.ensureEndModel(in.readInt());
    }
}