package org.owasp.netryx.mlcore.loss;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.serialize.MLComponent;

public interface LossFunction extends MLComponent {
    SimpleMatrix predict(SimpleMatrix X, SimpleMatrix coefficients);

    SimpleMatrix gradient(SimpleMatrix X, SimpleMatrix y, SimpleMatrix coefficients);

    double loss(SimpleMatrix y, SimpleMatrix predictions);
}