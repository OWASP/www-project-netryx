package org.owasp.netryx.mlcore.model.tree;

import org.owasp.netryx.mlcore.Regressor;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.prediction.LabelPrediction;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GradientBoosting implements Regressor {
    private final Object lock = new Object();

    private final IntegerHyperParameter numTrees;
    private final IntegerHyperParameter maxDepth;
    private final IntegerHyperParameter minSamplesSplit;
    private final DoubleHyperParameter learningRate;

    private List<DecisionTree> trees;
    private final ExecutorService executor;

    private double initialPrediction;

    private GradientBoosting(int numTrees, int maxDepth, int minSamplesSplit, double learningRate, int parallelism) {
        this.numTrees = new IntegerHyperParameter(numTrees, "numTrees");
        this.maxDepth = new IntegerHyperParameter(maxDepth, "maxDepth");
        this.minSamplesSplit = new IntegerHyperParameter(minSamplesSplit, "minSamplesSplit");
        this.learningRate = new DoubleHyperParameter(learningRate, "learningRate");

        this.trees = new ArrayList<>();

        this.executor = Executors.newFixedThreadPool(parallelism);
    }

    @Override
    public void fit(DataFrame X, DataFrame y) {
        initialPrediction = y.getColumn(0).castAsDouble().mean();
        var residuals = y.getColumn(0).castAsDouble().mapDouble(v -> v - initialPrediction);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (var i = 0; i < numTrees.getValue(); i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                var tree = new DecisionTree(maxDepth.getValue(), minSamplesSplit.getValue());
                tree.fit(X, new DataFrame(Map.of("residuals", residuals)));
                synchronized (lock) {
                    trees.add(tree);
                }

                var predictions = tree.predict(X);

                synchronized (residuals) {
                    for (var j = 0; j < residuals.size(); j++) {
                        var updatedResidual = residuals.get(j) - learningRate.getValue() * predictions.get(j).getLabel();
                        residuals.set(j, updatedResidual);
                    }
                }
            }, executor));
        }

        futures.forEach(CompletableFuture::join);
    }

    @Override
    public List<LabelPrediction> predict(DataFrame X) {
        List<CompletableFuture<List<Double>>> futures = new ArrayList<>();
        var initialPredictions = new ArrayList<>(Collections.nCopies(X.height(), initialPrediction));

        for (var tree : trees) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                var treePredictions = tree.predict(X);

                List<Double> updates = new ArrayList<>();
                for (var treePrediction : treePredictions) {
                    updates.add(learningRate.getValue() * treePrediction.getLabel());
                }
                return updates;
            }, executor));
        }

        var allUpdates = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        for (var i = 0; i < initialPredictions.size(); i++) {
            for (var updates : allUpdates) {
                initialPredictions.set(i, initialPredictions.get(i) + updates.get(i));
            }
        }

        return initialPredictions.stream().map(LabelPrediction::new).collect(Collectors.toList());
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return List.of(numTrees, maxDepth, minSamplesSplit, learningRate);
    }

    public static GradientBoostingBuilder newBuilder() {
        return new GradientBoostingBuilder();
    }

    public static GradientBoosting create() {
        return newBuilder().build();
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_MODEL);

        numTrees.save(out);
        maxDepth.save(out);
        minSamplesSplit.save(out);
        learningRate.save(out);

        out.writeDouble(initialPrediction);

        out.writeInt(trees.size());
        for (var tree : trees)
            tree.save(out);

        out.writeInt(MLFlag.END_MODEL);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartModel(in.readInt());

        numTrees.load(in);
        maxDepth.load(in);
        minSamplesSplit.load(in);
        learningRate.load(in);

        this.initialPrediction = in.readDouble();

        var treeCount = in.readInt();
        this.trees = new ArrayList<>(treeCount);

        for (var i = 0; i < treeCount; i++) {
            var tree = new DecisionTree(0, 0);
            tree.load(in);

            trees.add(tree);
        }

        MLFlag.ensureEndModel(in.readInt());
    }

    public static class GradientBoostingBuilder {
        private int numTrees = 100;
        private int maxDepth = 3;
        private int minSamplesSplit = 2;
        private double learningRate = 0.1;
        private int parallelism = Runtime.getRuntime().availableProcessors();

        public GradientBoostingBuilder setNumTrees(int numTrees) {
            this.numTrees = numTrees;
            return this;
        }

        public GradientBoostingBuilder setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public GradientBoostingBuilder setMinSamplesSplit(int minSamplesSplit) {
            this.minSamplesSplit = minSamplesSplit;
            return this;
        }

        public GradientBoostingBuilder setLearningRate(double learningRate) {
            this.learningRate = learningRate;
            return this;
        }

        public GradientBoostingBuilder setParallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public GradientBoosting build() {
            return new GradientBoosting(numTrees, maxDepth, minSamplesSplit, learningRate, parallelism);
        }
    }
}