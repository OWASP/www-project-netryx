package org.owasp.netryx.mlcore.loss;

import org.ejml.simple.SimpleMatrix;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LogisticLossFunction implements LossFunction {
    @Override
    public SimpleMatrix predict(SimpleMatrix X, SimpleMatrix coefficients) {
        return sigmoid(X.mult(coefficients));
    }

    @Override
    public SimpleMatrix gradient(SimpleMatrix X, SimpleMatrix y, SimpleMatrix coefficients) {
        var predictions = predict(X, coefficients);
        return X.transpose().mult(predictions.minus(y)).scale(1.0 / X.getNumRows());
    }

    @Override
    public double loss(SimpleMatrix y, SimpleMatrix predictions) {
        var m = y.getNumRows();

        return -1.0 / m * (y.elementMult(predictions.elementLog()).elementSum() +
                (y.negative().plus(1)).elementMult(predictions.negative().plus(1).elementLog()).elementSum());
    }

    private SimpleMatrix sigmoid(SimpleMatrix z) {
        var result = new SimpleMatrix(z);

        for (var i = 0; i < z.getNumRows(); i++) {
            for (var j = 0; j < z.getNumCols(); j++) {
                result.set(i, j, 1.0 / (1.0 + Math.exp(-z.get(i, j))));
            }
        }
        return result;
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        // nothing to store
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        // nothing to store
    }
}