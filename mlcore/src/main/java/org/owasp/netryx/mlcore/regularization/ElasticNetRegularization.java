package org.owasp.netryx.mlcore.regularization;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;

import java.util.Arrays;
import java.util.List;

public class ElasticNetRegularization implements Regularization {
    public static final String HYPER_PARAMETER_LAMBDA1 = "lambda1";
    public static final String HYPER_PARAMETER_LAMBDA2 = "lambda2";

    private final DoubleHyperParameter lambda1;
    private final DoubleHyperParameter lambda2;

    public ElasticNetRegularization(double lambda1, double lambda2) {
        this.lambda1 = new DoubleHyperParameter(lambda1, HYPER_PARAMETER_LAMBDA1);
        this.lambda2 = new DoubleHyperParameter(lambda2, HYPER_PARAMETER_LAMBDA2);
    }

    @Override
    public SimpleMatrix apply(SimpleMatrix coefficients) {
        return computeRegularizationTerm(coefficients, lambda1.getValue(), lambda2.getValue());
    }

    @Override
    public SimpleMatrix gradient(SimpleMatrix coefficients) {
        var regularizationTerm = computeRegularizationTerm(coefficients, lambda1.getValue(), lambda2.getValue());
        var l2Gradient = coefficients.copy();
        l2Gradient.set(0, 0, 0);

        return regularizationTerm.plus(l2Gradient.scale(lambda2.getValue()));
    }

    private SimpleMatrix computeRegularizationTerm(SimpleMatrix coefficients, double lambda1, double lambda2) {
        var l1Term = new SimpleMatrix(coefficients.getNumRows(), coefficients.getNumCols());

        for (var i = 0; i < coefficients.getNumRows(); i++)
            for (var j = 0; j < coefficients.getNumCols(); j++)
                l1Term.set(i, j, Math.signum(coefficients.get(i, j)));

        var l2Term = coefficients.copy();
        l1Term.set(0, 0, 0);
        l2Term.set(0, 0, 0);

        return l1Term.scale(lambda1).plus(l2Term.scale(lambda2));
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return Arrays.asList(lambda1, lambda2);
    }
}