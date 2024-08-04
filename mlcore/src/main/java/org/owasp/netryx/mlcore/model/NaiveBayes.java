package org.owasp.netryx.mlcore.model;

import org.owasp.netryx.mlcore.Classifier;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.prediction.ClassificationPrediction;
import org.owasp.netryx.mlcore.serialize.component.DoubleMapComponent;
import org.owasp.netryx.mlcore.serialize.component.LogFeatureProbabilityComponent;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;
import org.owasp.netryx.mlcore.util.DataUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NaiveBayes implements Classifier {
    public static final String HYPER_PARAMETER_ALPHA = "alpha";

    private Map<Double, Double> classProbabilities;
    private Map<Double, Map<Integer, Map<Double, Double>>> logFeatureProbabilities;

    private final ExecutorService executor;
    private final DoubleHyperParameter alpha;

    private NaiveBayes(int parallelism, double alpha) {
        this.executor = Executors.newFixedThreadPool(parallelism);
        this.alpha = new DoubleHyperParameter(alpha, HYPER_PARAMETER_ALPHA);
    }

    @Override
    public void fit(DataFrame X, DataFrame y) {
        var numSamples = X.height();
        var numFeatures = X.width();
        var ySeries =  y.getColumn(0).castAsDouble();

        classProbabilities = computeClassProbabilities(ySeries, numSamples);
        logFeatureProbabilities = new ConcurrentHashMap<>();

        for (double classLabel : classProbabilities.keySet()) {
            var classData = X.filterByIndex(row -> Objects.equals(y.getColumn(0).get(row), classLabel));
            var classFeatureProbabilities = computeFeatureProbabilities(classData, numFeatures);

            logFeatureProbabilities.put(classLabel, computeLogFeatureProbabilities(classFeatureProbabilities));
        }
    }

    @Override
    public List<ClassificationPrediction> predict(DataFrame X) {
        var numSamples = X.height();

        List<CompletableFuture<ClassificationPrediction>> futures = new ArrayList<>();
        for (var i = 0; i < numSamples; i++) {
            final var index = i;
            futures.add(CompletableFuture.supplyAsync(() -> predictSingleSample(X, index), executor));
        }

        return DataUtil.getPredictions(futures);
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return Collections.singletonList(alpha);
    }

    private Map<Double, Double> computeClassProbabilities(DoubleSeries ySeries, int numSamples) {
        return Arrays.stream(ySeries.getData().toArray(new Double[0]))
                .distinct()
                .collect(Collectors.toMap(
                        classLabel -> classLabel,
                        classLabel -> (double) ySeries.getData().stream().filter(value -> Objects.equals(value, classLabel)).count() / numSamples
                ));
    }

    private Map<Integer, Map<Double, Double>> computeFeatureProbabilities(DataFrame classData, int numFeatures) {
        Map<Integer, Map<Double, Double>> classFeatureProbabilities = new HashMap<>();

        for (var featureIndex = 0; featureIndex < numFeatures; featureIndex++) {
            var featureSeries = classData.getColumn(featureIndex).castAsDouble();
            Map<Double, Double> valueCounts = new HashMap<>();

            for (var value : featureSeries.getData()) {
                valueCounts.put(value, valueCounts.getOrDefault(value, 0.0) + 1);
            }

            double totalCount = featureSeries.size();
            double alphaValue = alpha.getValue();
            var probabilities = valueCounts.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> (entry.getValue() + alphaValue) / (totalCount + alphaValue * valueCounts.size())
                    ));

            classFeatureProbabilities.put(featureIndex, probabilities);
        }

        return classFeatureProbabilities;
    }

    private Map<Integer, Map<Double, Double>> computeLogFeatureProbabilities(Map<Integer, Map<Double, Double>> classFeatureProbabilities) {
        Map<Integer, Map<Double, Double>> logFeatureProbabilities = new HashMap<>();

        for (var entry : classFeatureProbabilities.entrySet()) {
            int featureIndex = entry.getKey();
            var probabilities = entry.getValue();
            var logProbabilities = probabilities.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            probEntry -> Math.log(probEntry.getValue())
                    ));

            logFeatureProbabilities.put(featureIndex, logProbabilities);
        }

        return logFeatureProbabilities;
    }

    private ClassificationPrediction predictSingleSample(DataFrame X, int sampleIndex) {
        var maxLogProbability = Double.NEGATIVE_INFINITY;
        double bestClass = -1;
        Map<Double, Double> classProbabilitiesMap = new HashMap<>();

        for (double classLabel : classProbabilities.keySet()) {
            var logProbability = Math.log(classProbabilities.get(classLabel));

            for (var featureIndex = 0; featureIndex < X.width(); featureIndex++) {
                var featureValue = X.getColumn(featureIndex).castAsDouble().getDouble(sampleIndex);
                var logFeatureProbabilityMap = logFeatureProbabilities.get(classLabel).get(featureIndex);

                double logFeatureProbability = logFeatureProbabilityMap.getOrDefault(featureValue, Math.log(alpha.getValue() / (logFeatureProbabilityMap.size() + 1)));

                logProbability += logFeatureProbability;
            }

            classProbabilitiesMap.put(classLabel, Math.exp(logProbability));

            if (logProbability > maxLogProbability) {
                maxLogProbability = logProbability;
                bestClass = classLabel;
            }
        }

        return new ClassificationPrediction(bestClass, classProbabilitiesMap);
    }

    public static NaiveBayesBuilder newBuilder() {
        return new NaiveBayesBuilder();
    }

    public static NaiveBayes create() {
        return newBuilder()
                .build();
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_MODEL);
        new DoubleMapComponent(classProbabilities).save(out);
        new LogFeatureProbabilityComponent(logFeatureProbabilities).save(out);

        alpha.save(out);
        out.writeInt(MLFlag.END_MODEL);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartModel(in.readInt());
        var classProbability = new DoubleMapComponent();
        classProbability.load(in);

        var logFeatures = new LogFeatureProbabilityComponent();
        logFeatures.load(in);

        alpha.load(in);

        MLFlag.ensureEndModel(in.readInt());

        this.classProbabilities = classProbability.getMap();
        this.logFeatureProbabilities = logFeatures.getMap();
    }

    public static class NaiveBayesBuilder {
        private int parallelism = Runtime.getRuntime().availableProcessors();
        private double alpha = 1.0;

        public NaiveBayesBuilder setAlpha(double alpha) {
            this.alpha = alpha;
            return this;
        }

        public NaiveBayesBuilder setParallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public NaiveBayes build() {
            if (alpha < 0)
                throw new IllegalArgumentException("Alpha should be >= 0");

            return new NaiveBayes(parallelism, alpha);
        }
    }
}