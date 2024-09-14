package org.owasp.netryx.mlcore.model;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.Classifier;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.loss.LogisticLossFunction;
import org.owasp.netryx.mlcore.loss.LossFunction;
import org.owasp.netryx.mlcore.optimizer.GradientDescent;
import org.owasp.netryx.mlcore.optimizer.Optimizer;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.prediction.ClassificationPrediction;
import org.owasp.netryx.mlcore.regularization.Regularization;
import org.owasp.netryx.mlcore.serialize.component.MatrixComponent;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;
import org.owasp.netryx.mlcore.util.DataUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogisticRegression implements Classifier {
    private SimpleMatrix coefficients;

    private final Optimizer optimizer;
    private final Regularization regularizer;

    private final LossFunction lossFunction;
    private final ExecutorService executor;

    private LogisticRegression(Optimizer optimizer, Regularization regularizer, int parallelism) {
        this.optimizer = optimizer;
        this.regularizer = regularizer;
        this.lossFunction = new LogisticLossFunction();
        this.executor = Executors.newFixedThreadPool(parallelism);
    }

    private LogisticRegression(Optimizer optimizer, int parallelism) {
        this(optimizer, null, parallelism);
    }

    @Override
    public void fit(DataFrame X, DataFrame y) {
        var matrixX = DataUtil.addIntercept(X);
        var vectorY = y.toSimpleMatrix();

        this.coefficients = optimizer.optimize(matrixX, vectorY, new SimpleMatrix(matrixX.getNumCols(), 1), lossFunction, regularizer);
    }

    @Override
    public List<ClassificationPrediction> predict(DataFrame x) {
        var numRows = x.height();
        List<CompletableFuture<ClassificationPrediction>> futures = new ArrayList<>();

        var input = DataUtil.addIntercept(x);
        var linearPredictions = input.mult(coefficients);

        for (var i = 0; i < numRows; i++) {
            var index = i;

            futures.add(CompletableFuture.supplyAsync(() -> {
                var probability = sigmoid(linearPredictions.get(index));
                var label = probability >= 0.5 ? 1.0 : 0.0;

                Map<Double, Double> probabilities = new HashMap<>();
                probabilities.put(0.0, 1.0 - probability);
                probabilities.put(1.0, probability);

                return new ClassificationPrediction(label, probabilities);
            }, executor));
        }

        return DataUtil.getPredictions(futures);
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        var hyperParams = new ArrayList<>(optimizer.getHyperParameters());

        if (regularizer != null)
            hyperParams.addAll(regularizer.getHyperParameters());

        return hyperParams;
    }

    private double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    public static LogisticRegressionBuilder newBuilder() {
        return new LogisticRegressionBuilder();
    }

    public static LogisticRegression create() {
        return newBuilder().build();
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_MODEL);
        new MatrixComponent(coefficients).save(out);

        optimizer.save(out);
        regularizer.save(out);
        lossFunction.save(out);

        out.writeInt(MLFlag.END_MODEL);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartModel(in.readInt());

        var coefficients = new MatrixComponent();
        coefficients.load(in);

        this.coefficients = coefficients.getMatrix();
        optimizer.load(in);
        regularizer.load(in);
        lossFunction.load(in);

        MLFlag.ensureEndModel(in.readInt());
    }

    public static class LogisticRegressionBuilder {
        private Optimizer optimizer = new GradientDescent(0.1, 1000);
        private Regularization regularizer = null;
        private int parallelism = Runtime.getRuntime().availableProcessors();

        public LogisticRegressionBuilder setOptimizer(Optimizer optimizer) {
            this.optimizer = optimizer;
            return this;
        }

        public LogisticRegressionBuilder setRegularizer(Regularization regularizer) {
            this.regularizer = regularizer;
            return this;
        }

        public LogisticRegressionBuilder setParallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public LogisticRegression build() {
            return new LogisticRegression(optimizer, regularizer, parallelism);
        }
    }
}