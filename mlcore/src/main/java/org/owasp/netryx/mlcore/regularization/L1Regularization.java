package org.owasp.netryx.mlcore.regularization;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;

import java.util.Collections;
import java.util.List;

public class L1Regularization implements Regularization {
    public static final String HYPER_PARAMETER_LAMBDA = "lambda";

    private final DoubleHyperParameter lambda;

    public L1Regularization(double lambda) {
        this.lambda = new DoubleHyperParameter(lambda, HYPER_PARAMETER_LAMBDA);
    }

    @Override
    public SimpleMatrix apply(SimpleMatrix coefficients) {
        return computeRegularizationTerm(coefficients, lambda.getValue());
    }

    @Override
    public SimpleMatrix gradient(SimpleMatrix coefficients) {
        return computeRegularizationTerm(coefficients, lambda.getValue());
    }

    private SimpleMatrix computeRegularizationTerm(SimpleMatrix coefficients, double lambda) {
        var regularizationTerm = new SimpleMatrix(coefficients.getNumRows(), coefficients.getNumCols());

        for (var i = 0; i < coefficients.getNumRows(); i++) {
            for (var j = 0; j < coefficients.getNumCols(); j++) {
                regularizationTerm.set(i, j, Math.signum(coefficients.get(i, j)));
            }
        }
        regularizationTerm.set(0, 0, 0);

        return regularizationTerm.scale(lambda);
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return Collections.singletonList(lambda);
    }
}