package org.owasp.netryx.mlcore.optimizer;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.loss.LossFunction;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.regularization.Regularization;

import java.util.List;

public interface Optimizer {
    SimpleMatrix optimize(SimpleMatrix X, SimpleMatrix y, SimpleMatrix initialCoefficients, LossFunction lossFunction, Regularization regularizer);

    List<HyperParameter<?>> getHyperParameters();
}