package org.owasp.netryx.mlcore.model.tree.node;

public class TreeNode {
    private boolean isLeaf;
    private double prediction;
    private int featureIndex;
    private double threshold;
    private TreeNode leftChild;
    private TreeNode rightChild;

    public TreeNode() {
        this.isLeaf = true;
    }

    public TreeNode(int featureIndex, double threshold) {
        this.isLeaf = false;
        this.featureIndex = featureIndex;
        this.threshold = threshold;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }

    public int getFeatureIndex() {
        return featureIndex;
    }

    public double getThreshold() {
        return threshold;
    }

    public TreeNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(TreeNode leftChild) {
        this.leftChild = leftChild;
    }

    public TreeNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(TreeNode rightChild) {
        this.rightChild = rightChild;
    }
}