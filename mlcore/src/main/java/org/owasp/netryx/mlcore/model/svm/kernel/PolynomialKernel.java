package org.owasp.netryx.mlcore.model.svm.kernel;

import org.ejml.simple.SimpleMatrix;

public class PolynomialKernel implements Kernel {
    private final double gamma;
    private final double coef0;
    private final int degree;

    public PolynomialKernel(double gamma, double coef0, int degree) {
        this.gamma = gamma;
        this.coef0 = coef0;
        this.degree = degree;
    }

    @Override
    public double apply(SimpleMatrix x1, SimpleMatrix x2) {
        return Math.pow(gamma * x1.dot(x2) + coef0, degree);
    }
}