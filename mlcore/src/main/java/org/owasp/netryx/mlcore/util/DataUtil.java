package org.owasp.netryx.mlcore.util;

import org.ejml.simple.SimpleMatrix;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.prediction.Prediction;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DataUtil {
    private DataUtil() {}

    public static SimpleMatrix addIntercept(DataFrame X) {
        var n = X.height();
        var m = X.width();

        var interceptX = new double[n][m + 1];

        for (var i = 0; i < n; i++) {
            interceptX[i][0] = 1.0;
            for (var j = 0; j < m; j++) {
                interceptX[i][j + 1] = X.getColumn(j).castAsDouble().getDouble(i);
            }
        }

        return new SimpleMatrix(interceptX);
    }

    public static <T extends Prediction> List<T> getPredictions(List<CompletableFuture<T>> futures) {
        var allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        var allPredictionsFuture = allOf.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));

        try {
            return allPredictionsFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error during prediction", e);
        }
    }
}