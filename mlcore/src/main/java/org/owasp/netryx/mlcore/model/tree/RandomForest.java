package org.owasp.netryx.mlcore.model.tree;

import org.owasp.netryx.mlcore.Regressor;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.prediction.LabelPrediction;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RandomForest implements Regressor {
    private final Object lock = new Object();

    public static final String HYPER_PARAM_NUM_TREES = "numTrees";
    public static final String HYPER_PARAM_MAX_DEPTH = "maxDepth";
    public static final String HYPER_PARAM_MIN_SAMPLES_SPLIT = "minSamplesSplit";
    public static final String HYPER_PARAM_NUM_FEATURES = "numFeatures";

    private final IntegerHyperParameter numTrees;
    private final IntegerHyperParameter maxDepth;
    private final IntegerHyperParameter minSamplesSplit;
    private final IntegerHyperParameter numFeatures;

    private List<DecisionTree> trees = new ArrayList<>();

    private final ExecutorService executor;

    private long randomState;
    private Random random;

    private RandomForest(int numTrees, int maxDepth, int minSamplesSplit, int numFeatures, int parallelism, Long randomState) {
        this.numTrees = new IntegerHyperParameter(numTrees, HYPER_PARAM_NUM_TREES);
        this.maxDepth = new IntegerHyperParameter(maxDepth, HYPER_PARAM_MAX_DEPTH);
        this.minSamplesSplit = new IntegerHyperParameter(minSamplesSplit, HYPER_PARAM_MIN_SAMPLES_SPLIT);
        this.numFeatures = new IntegerHyperParameter(numFeatures, HYPER_PARAM_NUM_FEATURES);

        this.executor = Executors.newFixedThreadPool(parallelism);
        this.randomState = randomState == null ? DEFAULT_RANDOM_SATE : randomState;

        this.random = new Random(this.randomState);
    }

    @Override
    public void fit(DataFrame X, DataFrame y) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (var i = 0; i < numTrees.getValue(); i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                var bootstrapSample = bootstrapSample(X, y);
                var tree = new DecisionTree(maxDepth.getValue(), minSamplesSplit.getValue());
                tree.fit(bootstrapSample[0], bootstrapSample[1]);

                synchronized (lock) {
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

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_MODEL);
        numTrees.save(out);
        maxDepth.save(out);
        minSamplesSplit.save(out);
        numFeatures.save(out);
        out.writeLong(randomState);

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
        numFeatures.load(in);

        var randomState = in.readLong();

        var size = in.readInt();
        var trees = new ArrayList<DecisionTree>(size);

        for (int i = 0; i < size; i++) {
            var tree = new DecisionTree(0, 0);
            tree.load(in);

            trees.add(tree);
        }

        this.trees = trees;
        this.randomState = randomState;
        this.random = new Random(randomState);

        MLFlag.ensureEndModel(in.readInt());
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

    public static final long DEFAULT_RANDOM_SATE = 94295;
}