package org.owasp.netryx.mlcore.loss;

import org.ejml.simple.SimpleMatrix;

public interface LossFunction {
    SimpleMatrix predict(SimpleMatrix X, SimpleMatrix coefficients);

    SimpleMatrix gradient(SimpleMatrix X, SimpleMatrix y, SimpleMatrix coefficients);

    double loss(SimpleMatrix y, SimpleMatrix predictions);
}