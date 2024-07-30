package org.owasp.netryx.mlcore.loss;

import org.ejml.simple.SimpleMatrix;

public class MeanSquaredError implements LossFunction {
    @Override
    public SimpleMatrix predict(SimpleMatrix X, SimpleMatrix coefficients) {
        return X.mult(coefficients);
    }

    @Override
    public SimpleMatrix gradient(SimpleMatrix X, SimpleMatrix y, SimpleMatrix coefficients) {
        var predictions = predict(X, coefficients);
        return X.transpose().mult(predictions.minus(y)).scale(2.0 / X.getNumRows());
    }

    @Override
    public double loss(SimpleMatrix y, SimpleMatrix predictions) {
        var diff = y.minus(predictions);
        return diff.elementMult(diff).elementSum() / y.getNumRows();
    }
}