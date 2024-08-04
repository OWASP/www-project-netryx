package org.owasp.netryx.mlcore.regularization;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.params.DoubleHyperParameter;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class L2Regularization implements Regularization {
    public static final String HYPER_PARAMETER_LAMBDA = "lambda";

    private final DoubleHyperParameter lambda;

    public L2Regularization(double lambda) {
        this.lambda = new DoubleHyperParameter(lambda, HYPER_PARAMETER_LAMBDA);
    }

    @Override
    public SimpleMatrix apply(SimpleMatrix coefficients) {
        var regularizationTerm = coefficients.copy();
        regularizationTerm.set(0, 0, 0);

        return regularizationTerm.scale(lambda.getValue());
    }

    @Override
    public SimpleMatrix gradient(SimpleMatrix coefficients) {
        var regularizationGradient = coefficients.copy();
        regularizationGradient.set(0, 0, 0);

        return regularizationGradient.scale(2 * lambda.getValue());
    }

    @Override
    public List<HyperParameter<?>> getHyperParameters() {
        return Collections.singletonList(lambda);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_REGULARIZER);
        out.writeDouble(lambda.getValue());
        out.writeInt(MLFlag.END_REGULARIZER);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartRegularization(in.readInt());

        var lambda = in.readDouble();
        this.lambda.setValue(lambda);

        MLFlag.ensureEndRegularization(in.readInt());
    }
}