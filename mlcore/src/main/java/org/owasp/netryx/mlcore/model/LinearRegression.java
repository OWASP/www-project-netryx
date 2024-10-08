package org.owasp.netryx.mlcore.model;

import org.ejml.data.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.Regressor;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.prediction.LabelPrediction;
import org.owasp.netryx.mlcore.regularization.Regularization;
import org.owasp.netryx.mlcore.serialize.component.MatrixComponent;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;
import org.owasp.netryx.mlcore.util.DataUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinearRegression implements Regressor {
    private SimpleMatrix coefficients;
    private final Regularization regularizer;

    private LinearRegression(Regularization regularizer) {
        this.regularizer = regularizer;
    }

    @Override
    public void fit(DataFrame X, DataFrame y) {
        var matrixX = DataUtil.addIntercept(X);
        var vectorY = y.toSimpleMatrix();

        var Xt = matrixX.transpose();
        var XtX = Xt.mult(matrixX);
        var Xty = Xt.mult(vectorY);

        try {
            if (regularizer != null) {
                this.coefficients = XtX.plus(regularizer.apply(XtX)).solve(Xty);
            } else {
                this.coefficients = XtX.solve(Xty);
            }
        } catch (SingularMatrixException e) {
            throw new IllegalArgumentException("Matrix is singular", e);
        }
    }

    @Override
    public List<LabelPrediction> predict(DataFrame x) {
        var matrixX = DataUtil.addIntercept(x);
        var predictions = matrixX.mult(coefficients);

        var result = new ArrayList<LabelPrediction>();

        for (var i = 0; i < predictions.getNumRows(); i++)
            result.add(new LabelPrediction(predictions.get(i, 0)));

        return result;
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        if (regularizer != null)
            return regularizer.getHyperParameters();

        return Collections.emptyList();
    }

    public SimpleMatrix getCoefficients() {
        return coefficients;
    }

    public static LinearRegression create(Regularization regularizer) {
        return new LinearRegression(regularizer);
    }

    public static LinearRegression create() {
        return create(null);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_MODEL);

        new MatrixComponent(coefficients).save(out);
        regularizer.save(out);

        out.writeInt(MLFlag.END_MODEL);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartModel(in.readInt());

        var matrix = new MatrixComponent();
        matrix.load(in);
        this.coefficients = matrix.getMatrix();

        regularizer.load(in);
        MLFlag.ensureEndModel(in.readInt());
    }
}