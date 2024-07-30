package org.owasp.netryx.mlcore.regularization;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.params.HyperParameter;

import java.util.List;

public interface Regularization {
    SimpleMatrix apply(SimpleMatrix coefficients);

    SimpleMatrix gradient(SimpleMatrix coefficients);

    List<HyperParameter<?>> getHyperParameters();
}