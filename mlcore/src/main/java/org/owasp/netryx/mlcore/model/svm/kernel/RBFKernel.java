package org.owasp.netryx.mlcore.model.svm.kernel;

import org.ejml.simple.SimpleMatrix;

public class RBFKernel implements Kernel {
    private final double gamma;

    public RBFKernel(double gamma) {
        this.gamma = gamma;
    }

    @Override
    public double apply(SimpleMatrix x1, SimpleMatrix x2) {
        var diff = x1.minus(x2);
        return Math.exp(-gamma * diff.dot(diff));
    }
}