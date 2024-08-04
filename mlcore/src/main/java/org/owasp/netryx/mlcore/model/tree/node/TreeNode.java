package org.owasp.netryx.mlcore.model.tree.node;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TreeNode implements MLComponent {
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

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeBoolean(isLeaf);

        if (isLeaf) {
            out.writeDouble(prediction);
        } else {
            out.writeInt(featureIndex);
            out.writeDouble(threshold);
            if (leftChild != null) {
                leftChild.save(out);
            } else {
                out.writeBoolean(false);
            }
            if (rightChild != null) {
                rightChild.save(out);
            } else {
                out.writeBoolean(false);
            }
        }
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        isLeaf = in.readBoolean();
        if (isLeaf) {
            prediction = in.readDouble();
        } else {
            featureIndex = in.readInt();
            threshold = in.readDouble();

            boolean hasLeftChild = in.readBoolean();
            if (hasLeftChild) {
                leftChild = new TreeNode();
                leftChild.load(in);
            }

            boolean hasRightChild = in.readBoolean();
            if (hasRightChild) {
                rightChild = new TreeNode();
                rightChild.load(in);
            }
        }
    }
}