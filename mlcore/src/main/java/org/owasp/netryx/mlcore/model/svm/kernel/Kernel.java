package org.owasp.netryx.mlcore.model.svm.kernel;

import org.ejml.simple.SimpleMatrix;

public interface Kernel {
    double apply(SimpleMatrix x1, SimpleMatrix x2);
}