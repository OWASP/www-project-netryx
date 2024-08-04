package org.owasp.netryx.mlcore.serialize.component;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.serialize.MLComponent;
import org.owasp.netryx.mlcore.serialize.flag.MLFlag;

import java.io.*;

public class MatrixComponent implements MLComponent {
    private SimpleMatrix matrix;

    public MatrixComponent() {}

    public MatrixComponent(SimpleMatrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(MLFlag.START_MATRIX);
        out.writeInt(matrix.getNumRows());
        out.writeInt(matrix.getNumCols());

        for (int i = 0; i < matrix.getNumRows(); i++) {
            for (int j = 0; j < matrix.getNumRows(); j++) {
                out.writeDouble(matrix.get(i, j));
            }
        }

        out.writeInt(MLFlag.END_MATRIX);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        MLFlag.ensureStartMatrix(in.readInt());

        var numRows = in.readInt();
        var numCols = in.readInt();

        var data = new double[numCols][numRows];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                data[j][i] = in.readInt();
            }
        }

        MLFlag.ensureEndMatrix(in.readInt());

        this.matrix = new SimpleMatrix(data);
    }

    public SimpleMatrix getMatrix() {
        return matrix;
    }
}
