package org.owasp.netryx.mlcore.optimizer;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.loss.LossFunction;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.params.IntegerHyperParameter;
import org.owasp.netryx.mlcore.regularization.Regularization;

import java.util.Arrays;
import java.util.List;

public class AdamOptimizer implements Optimizer {
    public static final String HYPER_PARAMETER_LEARNING_RATE = "learningRate";
    public static final String HYPER_PARAMETER_ITERATIONS = "iterations";
    public static final String HYPER_PARAMETER_BETA1 = "beta1";
    public static final String HYPER_PARAMETER_BETA2 = "beta2";
    public static final String HYPER_PARAMETER_EPSILON = "epsilon";

    private final DoubleHyperParameter learningRate;
    private final IntegerHyperParameter iterations;
    private final DoubleHyperParameter beta1;
    private final DoubleHyperParameter beta2;
    private final DoubleHyperParameter epsilon;

    public AdamOptimizer(double learningRate, int iterations, double beta1, double beta2, double epsilon) {
        this.learningRate = new DoubleHyperParameter(learningRate, HYPER_PARAMETER_LEARNING_RATE);
        this.iterations = new IntegerHyperParameter(iterations, HYPER_PARAMETER_ITERATIONS);
        this.beta1 = new DoubleHyperParameter(beta1, HYPER_PARAMETER_BETA1);
        this.beta2 = new DoubleHyperParameter(beta2, HYPER_PARAMETER_BETA2);
        this.epsilon = new DoubleHyperParameter(epsilon, HYPER_PARAMETER_EPSILON);
    }

    @Override
    public SimpleMatrix optimize(SimpleMatrix X, SimpleMatrix y, SimpleMatrix initialCoefficients, LossFunction lossFunction, Regularization regularizer) {
        var numFeatures = initialCoefficients.getNumRows();

        var coefficients = initialCoefficients.copy();

        var m = new SimpleMatrix(numFeatures, 1);
        var v = new SimpleMatrix(numFeatures, 1);

        for (var t = 1; t <= iterations.getValue(); t++) {
            var gradient = lossFunction.gradient(X, y, coefficients);

            if (regularizer != null)
                gradient = gradient.plus(regularizer.gradient(coefficients));

            m = m.scale(beta1.getValue()).plus(gradient.scale(1 - beta1.getValue()));
            v = v.scale(beta2.getValue()).plus(gradient.elementPower(2).scale(1 - beta2.getValue()));

            var mHat = m.scale(1.0 / (1 - Math.pow(beta1.getValue(), t)));
            var vHat = v.scale(1.0 / (1 - Math.pow(beta2.getValue(), t)));

            coefficients = coefficients.minus(mHat.elementDiv(vHat.elementPower(0.5).plus(epsilon.getValue()))
                    .scale(learningRate.getValue()));
        }

        return coefficients;
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return Arrays.asList(learningRate, iterations, beta1, beta2, epsilon);
    }
}