package org.owasp.netryx.mlcore.model.svm;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.Classifier;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.model.svm.kernel.Kernel;
import org.owasp.netryx.mlcore.model.svm.kernel.LinearKernel;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.prediction.ClassificationPrediction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class KernelSVM implements Classifier {
    private SimpleMatrix alpha;
    private double b;

    private SimpleMatrix supportVectors;
    private SimpleMatrix supportLabels;

    private final DoubleHyperParameter C;
    private final DoubleHyperParameter tol;
    private final IntegerHyperParameter maxPasses;
    private final Kernel kernel;
    private final ExecutorService executor;

    public KernelSVM(Kernel kernel, double C, double tol, int maxPasses, int parallelism) {
        this.kernel = kernel;
        this.C = new DoubleHyperParameter(C, "C");
        this.tol = new DoubleHyperParameter(tol, "tol");
        this.maxPasses = new IntegerHyperParameter(maxPasses, "maxPasses");

        this.executor = Executors.newFixedThreadPool(parallelism);
    }

    @Override
    public void fit(DataFrame x, DataFrame y) {
        var X = x.toSimpleMatrix();
        var Y = y.toSimpleMatrix();
        var numRows = X.getNumRows();

        alpha = new SimpleMatrix(numRows, 1);
        b = 0;
        var passes = 0;

        while (passes < maxPasses.getValue()) {
            var numChangedAlphas = 0;
            for (var i = 0; i < numRows; i++) {
                numChangedAlphas += examineExample(i, X, Y);
            }
            passes = (numChangedAlphas == 0) ? passes + 1 : 0;
        }

        extractSupportVectors(X, Y);
    }

    @Override
    public List<ClassificationPrediction> predict(DataFrame x) {
        var X = x.toSimpleMatrix();
        List<CompletableFuture<ClassificationPrediction>> futures = new ArrayList<>();

        for (var i = 0; i < X.getNumRows(); i++) {
            var finalI = i;
            futures.add(CompletableFuture.supplyAsync(() -> {
                var prediction = calculatePrediction(X.extractVector(true, finalI), supportVectors, supportLabels);
                double label = prediction > 0 ? 1 : -1;
                return new ClassificationPrediction(label, null);
            }, executor));
        }

        return futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return List.of(C, tol, maxPasses);
    }

    private int examineExample(int i2, SimpleMatrix X, SimpleMatrix Y) {
        var y2 = Y.get(i2);
        var alpha2 = alpha.get(i2);
        var E2 = calculateError(i2, X, Y);
        var r2 = E2 * y2;

        if ((r2 < -tol.getValue() && alpha2 < C.getValue()) || (r2 > tol.getValue() && alpha2 > 0)) {
            for (var i1 = 0; i1 < X.getNumRows(); i1++) {
                if (i1 != i2 && takeStep(i1, i2, X, Y)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    private boolean takeStep(int i1, int i2, SimpleMatrix X, SimpleMatrix Y) {
        if (i1 == i2) return false;

        var alpha1 = alpha.get(i1);
        var alpha2 = alpha.get(i2);
        var y1 = Y.get(i1);
        var y2 = Y.get(i2);
        var E1 = calculateError(i1, X, Y);
        var E2 = calculateError(i2, X, Y);

        var s = y1 * y2;
        double L, H;

        if (y1 != y2) {
            L = Math.max(0, alpha2 - alpha1);
            H = Math.min(C.getValue(), C.getValue() + alpha2 - alpha1);
        } else {
            L = Math.max(0, alpha2 + alpha1 - C.getValue());
            H = Math.min(C.getValue(), alpha2 + alpha1);
        }

        if (L == H) return false;

        var k11 = kernel.apply(X.extractVector(true, i1), X.extractVector(true, i1));
        var k12 = kernel.apply(X.extractVector(true, i1), X.extractVector(true, i2));
        var k22 = kernel.apply(X.extractVector(true, i2), X.extractVector(true, i2));
        var eta = k11 + k22 - 2 * k12;

        if (eta <= 0) return false;

        var newAlpha2 = alpha2 + y2 * (E1 - E2) / eta;
        newAlpha2 = Math.min(Math.max(newAlpha2, L), H);

        if (Math.abs(newAlpha2 - alpha2) < tol.getValue() * (newAlpha2 + alpha2 + tol.getValue()))
            return false;

        var newAlpha1 = alpha1 + s * (alpha2 - newAlpha2);

        double oldAlpha1 = alpha.get(i1);
        double oldAlpha2 = alpha.get(i2);

        alpha.set(i1, newAlpha1);
        alpha.set(i2, newAlpha2);

        updateBias(E1, E2, i1, i2, newAlpha1, newAlpha2, oldAlpha1, oldAlpha2, X, Y);

        return true;
    }

    private void updateBias(double E1, double E2, int i1, int i2, double newAlpha1, double newAlpha2, double oldAlpha1, double oldAlpha2, SimpleMatrix X, SimpleMatrix Y) {
        var b1 = b - E1
                - Y.get(i1) * (newAlpha1 - oldAlpha1) * kernel.apply(X.extractVector(true, i1), X.extractVector(true, i1))
                - Y.get(i2) * (newAlpha2 - oldAlpha2) * kernel.apply(X.extractVector(true, i1), X.extractVector(true, i2));

        var b2 = b - E2
                - Y.get(i1) * (newAlpha1 - oldAlpha1) * kernel.apply(X.extractVector(true, i1), X.extractVector(true, i2))
                - Y.get(i2) * (newAlpha2 - oldAlpha2) * kernel.apply(X.extractVector(true, i2), X.extractVector(true, i2));

        if (0 < newAlpha1 && newAlpha1 < C.getValue()) {
            b = b1;
        } else if (0 < newAlpha2 && newAlpha2 < C.getValue()) {
            b = b2;
        } else {
            b = (b1 + b2) / 2;
        }
    }

    private double calculatePrediction(SimpleMatrix x, SimpleMatrix X, SimpleMatrix Y) {
        double sum = 0;
        for (var j = 0; j < X.getNumRows(); j++) {
            sum += alpha.get(j) * Y.get(j) * kernel.apply(x, X.extractVector(true, j));
        }
        return sum + b;
    }

    private double calculateError(int i, SimpleMatrix X, SimpleMatrix Y) {
        var xi = X.extractVector(true, i);
        return calculatePrediction(xi, X, Y) - Y.get(i);
    }

    private void extractSupportVectors(SimpleMatrix X, SimpleMatrix Y) {
        List<Integer> supportIndices = new ArrayList<>();
        for (var i = 0; i < alpha.getNumRows(); i++) {
            if (alpha.get(i) > 0) {
                supportIndices.add(i);
            }
        }

        var supportSize = supportIndices.size();

        supportVectors = new SimpleMatrix(supportSize, X.getNumCols());
        supportLabels = new SimpleMatrix(supportSize, 1);

        var supportAlpha = new SimpleMatrix(supportSize, 1);

        for (var i = 0; i < supportSize; i++) {
            int idx = supportIndices.get(i);
            supportVectors.insertIntoThis(i, 0, X.extractVector(true, idx));
            supportLabels.set(i, 0, Y.get(idx));
            supportAlpha.set(i, 0, alpha.get(idx));
        }

        alpha = supportAlpha;
    }

    public static KernelSVMBuilder newBuilder() {
        return new KernelSVMBuilder();
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        throw new UnsupportedOperationException("KernelSVM is in Beta");
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        throw new UnsupportedOperationException("KernelSVM is in Beta");
    }

    public static class KernelSVMBuilder {
        private Kernel kernel = new LinearKernel();
        private double C = 1.0;
        private double tol = 1e-3;
        private int maxPasses = 100;
        private int parallelism = Runtime.getRuntime().availableProcessors();

        public KernelSVMBuilder setKernel(Kernel kernel) {
            this.kernel = kernel;
            return this;
        }

        public KernelSVMBuilder setC(double C) {
            this.C = C;
            return this;
        }

        public KernelSVMBuilder setTol(double tol) {
            this.tol = tol;
            return this;
        }

        public KernelSVMBuilder setMaxPasses(int maxPasses) {
            this.maxPasses = maxPasses;
            return this;
        }

        public KernelSVMBuilder setParallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public KernelSVM build() {
            return new KernelSVM(kernel, C, tol, maxPasses, parallelism);
        }
    }
}