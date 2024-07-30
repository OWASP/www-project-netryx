package org.owasp.netryx.mlcore.model.knn;

import org.owasp.netryx.mlcore.Classifier;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.prediction.ClassificationPrediction;

import java.util.*;
import java.util.stream.Collectors;

public class KNNClassifier implements Classifier {
    public static final String HYPER_PARAMETER_K = "k";

    private KDTree kdTree;
    private Map<double[], Double> instanceLabelMap;

    private final IntegerHyperParameter k;

    private KNNClassifier(int k) {
        this.k = new IntegerHyperParameter(k, HYPER_PARAMETER_K);
    }

    @Override
    public void fit(DataFrame X, DataFrame y) {
        var numCols = X.width();

        kdTree = new KDTree(numCols);
        instanceLabelMap = new HashMap<>();

        var xMatrix = X.toSimpleMatrix();
        var ySeries = y.getColumn(0).castAsDouble();

        for (var i = 0; i < xMatrix.getNumRows(); i++) {
            var instance = xMatrix.extractVector(true, i).getDDRM().data;
            kdTree.insert(instance);
            instanceLabelMap.put(instance, ySeries.getDouble(i));
        }
    }

    @Override
    public List<ClassificationPrediction> predict(DataFrame x) {
        var numRows = x.height();
        var predictions = new ArrayList<ClassificationPrediction>(numRows);

        var xMatrix = x.toSimpleMatrix();

        for (var i = 0; i < numRows; i++) {
            var currentPoint = xMatrix.extractVector(true, i).getDDRM().data;
            var neighbors = kdTree.nearestNeighbors(currentPoint, k.getValue());

            List<Double> neighborLabels = new ArrayList<>();

            for (var neighbor : neighbors)
                neighborLabels.add(instanceLabelMap.get(neighbor));

            var prediction = getMajorityClass(neighborLabels);
            predictions.add(new ClassificationPrediction(prediction, getLabelProbabilities(neighborLabels)));
        }

        return predictions;
    }

    private double getMajorityClass(List<Double> neighbors) {
        var classCounts = neighbors.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        long maxCount = Collections.max(classCounts.values());

        var candidates = classCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Collections.shuffle(candidates);

        return candidates.get(0);
    }

    private Map<Double, Double> getLabelProbabilities(List<Double> neighbors) {
        var classCounts = neighbors.stream()
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

        long totalCount = neighbors.size();
        Map<Double, Double> probabilities = new HashMap<>();
        for (var entry : classCounts.entrySet()) {
            probabilities.put(entry.getKey(), entry.getValue() / (double) totalCount);
        }

        return probabilities;
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return Collections.singletonList(k);
    }

    public static KNNClassifier create(int k) {
        return new KNNClassifier(k);
    }
}
