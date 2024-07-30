package org.owasp.netryx.mlcore.metrics;

public class EvaluationMetrics {
    private final double accuracy;
    private final double precision;
    private final double recall;
    private final double f1Score;

    public EvaluationMetrics(double accuracy, double precision, double recall, double f1Score) {
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1Score = f1Score;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getF1Score() {
        return f1Score;
    }

    @Override
    public String toString() {
        return "EvaluationMetrics{" +
                "accuracy=" + accuracy +
                ", precision=" + precision +
                ", recall=" + recall +
                ", f1Score=" + f1Score +
                '}';
    }
}