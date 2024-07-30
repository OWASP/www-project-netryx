package org.owasp.netryx.mlcore.prediction;

public class LabelPrediction implements Prediction {
    private final double value;

    public LabelPrediction(double value) {
        this.value = value;
    }

    @Override
    public double getLabel() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
