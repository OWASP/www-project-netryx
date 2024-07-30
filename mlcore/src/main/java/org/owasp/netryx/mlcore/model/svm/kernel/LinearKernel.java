package org.owasp.netryx.mlcore.model.svm.kernel;

import org.ejml.simple.SimpleMatrix;

public class LinearKernel implements Kernel {
    @Override
    public double apply(SimpleMatrix x1, SimpleMatrix x2) {
        return x1.dot(x2);
    }
}