package org.owasp.netryx.mlcore.regularization;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.util.List;

public interface Regularization extends MLComponent {
    SimpleMatrix apply(SimpleMatrix coefficients);

    SimpleMatrix gradient(SimpleMatrix coefficients);

    List<HyperParameter<?>> getHyperParameters();
}