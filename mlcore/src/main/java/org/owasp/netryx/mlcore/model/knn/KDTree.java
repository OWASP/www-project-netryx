package org.owasp.netryx.mlcore.model.knn;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class KDTree implements MLComponent {
    private KDNode root;
    private int k;

    public KDTree(int k) {
        this.k = k;
    }

    public void insert(double[] point) {
        root = insertRec(root, point, 0);
    }

    private KDNode insertRec(KDNode root, double[] point, int depth) {
        if (root == null) {
            return new KDNode(point);
        }

        var cd = depth % k;

        if (point[cd] < root.getPoint()[cd]) {
            root.setLeft(insertRec(root.getLeft(), point, depth + 1));
        } else {
            root.setRight(insertRec(root.getRight(), point, depth + 1));
        }

        return root;
    }

    public List<double[]> nearestNeighbors(double[] point, int n) {
        var pq = new PriorityQueue<KDNodeDist>(Comparator.comparingDouble(a -> -a.distance));
        nearestRec(root, point, 0, pq, n);

        List<double[]> neighbors = new ArrayList<>();
        while (!pq.isEmpty()) {
            neighbors.add(pq.poll().node().getPoint());
        }
        Collections.reverse(neighbors);
        return neighbors;
    }

    private void nearestRec(KDNode root, double[] point, int depth, PriorityQueue<KDNodeDist> pq, int n) {
        if (root == null) {
            return;
        }

        var dist = distance(root.getPoint(), point);
        pq.offer(new KDNodeDist(root, dist));

        if (pq.size() > n) {
            pq.poll();
        }

        var cd = depth % k;
        var next = point[cd] < root.getPoint()[cd] ? root.getLeft() : root.getRight();
        var other = point[cd] < root.getPoint()[cd] ? root.getRight() : root.getLeft();

        nearestRec(next, point, depth + 1, pq, n);

        if (pq.size() < n || Math.abs(root.getPoint()[cd] - point[cd]) < pq.peek().distance()) {
            nearestRec(other, point, depth + 1, pq, n);
        }
    }

    private double distance(double[] a, double[] b) {
        double dist = 0;

        for (var i = 0; i < k; i++) {
            dist += (a[i] - b[i]) * (a[i] - b[i]);
        }

        return Math.sqrt(dist);
    }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(k);
        root.save(out);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        k = in.readInt();

        root = new KDNode();
        root.load(in);
    }

    public static class KDNodeDist {
        private final KDNode node;
        private final double distance;

        public KDNodeDist(KDNode node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        public KDNode node() {
            return node;
        }

        public double distance() {
            return distance;
        }
    }
}