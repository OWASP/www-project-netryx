package org.owasp.netryx.mlcore.model.tree;

import org.owasp.netryx.mlcore.Regressor;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.prediction.LabelPrediction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RandomForest implements Regressor {
    public static final String HYPER_PARAM_NUM_TREES = "numTrees";
    public static final String HYPER_PARAM_MAX_DEPTH = "maxDepth";
    public static final String HYPER_PARAM_MIN_SAMPLES_SPLIT = "minSamplesSplit";
    public static final String HYPER_PARAM_NUM_FEATURES = "numFeatures";

    private final IntegerHyperParameter numTrees;
    private final IntegerHyperParameter maxDepth;
    private final IntegerHyperParameter minSamplesSplit;
    private final IntegerHyperParameter numFeatures;

    private final List<DecisionTree> trees;
    private final ExecutorService executor;
    private final Random random;

    private RandomForest(int numTrees, int maxDepth, int minSamplesSplit, int numFeatures, int parallelism, Long randomState) {
        this.numTrees = new IntegerHyperParameter(numTrees, HYPER_PARAM_NUM_TREES);
        this.maxDepth = new IntegerHyperParameter(maxDepth, HYPER_PARAM_MAX_DEPTH);
        this.minSamplesSplit = new IntegerHyperParameter(minSamplesSplit, HYPER_PARAM_MIN_SAMPLES_SPLIT);
        this.numFeatures = new IntegerHyperParameter(numFeatures, HYPER_PARAM_NUM_FEATURES);

        this.trees = new ArrayList<>();
        this.executor = Executors.newFixedThreadPool(parallelism);
        this.random = randomState == null ? new Random() : new Random(randomState);
    }

    @Override
    public void fit(DataFrame X, DataFrame y) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (var i = 0; i < numTrees.getValue(); i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                var bootstrapSample = bootstrapSample(X, y);
                var tree = new DecisionTree(maxDepth.getValue(), minSamplesSplit.getValue());
                tree.fit(bootstrapSample[0], bootstrapSample[1]);

                synchronized (trees) {
                    trees.add(tree);
                }
            }, executor));
        }

        futures.forEach(CompletableFuture::join);
    }

    @Override
    public List<LabelPrediction> predict(DataFrame X) {
        List<CompletableFuture<List<LabelPrediction>>> futures = new ArrayList<>();
        for (var tree : trees) {
            futures.add(CompletableFuture.supplyAsync(() -> tree.predict(X), executor));
        }

        var allPredictions = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        List<LabelPrediction> finalPredictions = new ArrayList<>();
        for (var i = 0; i < X.height(); i++) {
            double sum = 0;
            for (var predictions : allPredictions) {
                sum += predictions.get(i).getLabel();
            }
            finalPredictions.add(new LabelPrediction(sum / numTrees.getValue()));
        }
        return finalPredictions;
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return List.of(numTrees, maxDepth, minSamplesSplit, numFeatures);
    }

    private DataFrame[] bootstrapSample(DataFrame X, DataFrame y) {
        var n = X.height();
        if (n == 0) {
            return new DataFrame[]{X, y};
        }

        var indices = random.ints(n, 0, n).boxed().collect(Collectors.toList());
        var XSample = X.selectRows(indices);
        var ySample = y.selectRows(indices);
        return new DataFrame[]{XSample, ySample};
    }

    public List<DecisionTree> getTrees() {
        return trees;
    }

    public static RandomForestBuilder newBuilder() {
        return new RandomForestBuilder();
    }

    public static RandomForest create() {
        return newBuilder().build();
    }

    public static class RandomForestBuilder {
        private int numTrees = 10;
        private int maxDepth = 5;
        private int minSamplesSplit = 2;
        private int numFeatures = 3;
        private int parallelism = 4;
        private Long randomState = null;

        public RandomForestBuilder setNumTrees(int numTrees) {
            this.numTrees = numTrees;
            return this;
        }

        public RandomForestBuilder setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public RandomForestBuilder setMinSamplesSplit(int minSamplesSplit) {
            this.minSamplesSplit = minSamplesSplit;
            return this;
        }

        public RandomForestBuilder setNumFeatures(int numFeatures) {
            this.numFeatures = numFeatures;
            return this;
        }

        public RandomForestBuilder setParallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public RandomForestBuilder setRandomState(Long randomState) {
            this.randomState = randomState;
            return this;
        }

        public RandomForest build() {
            return new RandomForest(numTrees, maxDepth, minSamplesSplit, numFeatures, parallelism, randomState);
        }
    }
}