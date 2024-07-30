package org.owasp.netryx.mlcore.prediction;

import java.util.Map;

public class ClassificationPrediction implements ProbabilityPrediction {
    private final double label;
    private final Map<Double, Double> probabilities;

    public ClassificationPrediction(double label, Map<Double, Double> probabilities) {
        this.label = label;
        this.probabilities = probabilities;
    }

    @Override
    public double getLabel() {
        return label;
    }

    @Override
    public Map<Double, Double> getProbabilities() {
        return probabilities;
    }

    @Override
    public String toString() {
        return label + ":" + probabilities;
    }
}