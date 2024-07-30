package org.owasp.netryx.mlcore.prediction;

import java.util.Map;

public interface ProbabilityPrediction extends Prediction {
    Map<Double, Double> getProbabilities();
}