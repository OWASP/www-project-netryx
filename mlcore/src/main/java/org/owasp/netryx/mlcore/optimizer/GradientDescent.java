package org.owasp.netryx.mlcore.optimizer;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.loss.LossFunction;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.regularization.Regularization;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GradientDescent implements Optimizer {
    public static final String HYPER_PARAMETER_LEARNING_RATE = "learningRate";
    public static final String HYPER_PARAMETER_ITERATIONS = "iterations";

    private final DoubleHyperParameter learningRate;
    private final IntegerHyperParameter iterations;

    public GradientDescent(double learningRate, int iterations) {
        this.learningRate = new DoubleHyperParameter(learningRate, HYPER_PARAMETER_LEARNING_RATE);
        this.iterations = new IntegerHyperParameter(iterations, HYPER_PARAMETER_ITERATIONS);
    }

    public GradientDescent() {
        this(0.1, 1000);
    }

    @Override
    public SimpleMatrix optimize(SimpleMatrix X, SimpleMatrix y, SimpleMatrix initialCoefficients, LossFunction lossFunction, Regularization regularizer) {
        var coefficients = initialCoefficients.copy();

        for (var i = 0; i < iterations.getValue(); i++) {
            var gradient = lossFunction.gradient(X, y, coefficients);

            if (regularizer != null)
                gradient = gradient.plus(regularizer.gradient(coefficients));

            coefficients = coefficients.minus(gradient.scale(learningRate.getValue()));
        }

        return coefficients;
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return Arrays.asList(learningRate, iterations);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_OPTIMIZER);

        learningRate.save(out);
        iterations.save(out);

        out.writeInt(MLFlag.END_OPTIMIZER);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartOptimizer(in.readInt());

        learningRate.load(in);
        iterations.load(in);

        MLFlag.ensureEndOptimizer(in.readInt());
    }
}