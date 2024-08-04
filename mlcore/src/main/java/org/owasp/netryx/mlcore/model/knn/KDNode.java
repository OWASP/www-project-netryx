package org.owasp.netryx.mlcore.model.knn;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KDNode implements MLComponent {
    private double[] point;

    private KDNode left;
    private KDNode right;

    public KDNode(double[] point) {
        this.point = point;
        this.left = null;
        this.right = null;
    }

    public KDNode() {
        this(new double[0]);
    }

    public double[] getPoint() {
        return point;
    }

    public KDNode getLeft() {
        return left;
    }

    public void setLeft(KDNode left) {
        this.left = left;
    }

    public KDNode getRight() {
        return right;
    }

    public void setRight(KDNode right) {
        this.right = right;
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(point.length);

        for (var d : point)
            out.writeDouble(d);

        var hasLeftChild = left != null;
        var hasRightChild = right != null;

        out.writeBoolean(hasLeftChild);
        if (hasLeftChild) left.save(out);

        out.writeBoolean(hasRightChild);
        if (hasRightChild) right.save(out);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        var pointSize = in.readInt();

        point = new double[pointSize];

        for (var i = 0; i < pointSize; i++)
            point[i] = in.readDouble();

        var hasLeftChild = in.readBoolean();

        if (hasLeftChild) {
            left = new KDNode();
            left.load(in);
        }

        var hasRightChild = in.readBoolean();

        if (hasRightChild) {
            right = new KDNode();
            right.load(in);
        }
    }
}
