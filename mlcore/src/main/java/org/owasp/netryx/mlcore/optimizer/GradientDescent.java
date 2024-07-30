package org.owasp.netryx.mlcore.optimizer;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.loss.LossFunction;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.regularization.Regularization;

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
}