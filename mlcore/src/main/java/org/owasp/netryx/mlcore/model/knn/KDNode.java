package org.owasp.netryx.mlcore.model.knn;

public class KDNode {
    private final double[] point;

    private KDNode left;
    private KDNode right;

    public KDNode(double[] point) {
        this.point = point;
        this.left = null;
        this.right = null;
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
}
