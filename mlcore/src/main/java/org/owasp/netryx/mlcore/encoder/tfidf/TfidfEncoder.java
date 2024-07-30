package org.owasp.netryx.mlcore.encoder.tfidf;

import org.owasp.netryx.mlcore.encoder.Encoder;
import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TfidfEncoder implements Encoder {
    private String columnName;
    private Map<String, Double> idfValues;
    private final NGram nGram;
    private final Executor executor;

    public TfidfEncoder(NGram nGram, int parallelism) {
        this.nGram = nGram;
        this.executor = Executors.newFixedThreadPool(parallelism);
    }

    public Map<String, Double> getIdfValues() {
        return idfValues;
    }

    @Override
    public void fit(DataFrame df, String columnName) {
        this.columnName = columnName;
        this.idfValues = new ConcurrentHashMap<>();

        var documents = df.getColumn(columnName).castAs(String.class).getData();
        var numDocuments = documents.size();
        var documentFrequencies = computeDocumentFrequencies(documents);

        computeIdfValues(documentFrequencies, numDocuments);
    }

    @Override
    public DataFrame transform(DataFrame df) {
        var documents = df.getColumn(columnName).castAs(String.class).getData();
        var tfidfVectors = computeTfidfVectors(documents);

        return createTransformedDataFrame(df, tfidfVectors);
    }

    private Map<String, Integer> computeDocumentFrequencies(List<String> documents) {
        var documentFrequencies = new ConcurrentHashMap<String, Integer>();

        CompletableFuture.allOf(documents.stream()
                        .map(document -> CompletableFuture.runAsync(() -> {
                            Set<String> uniqueTerms = new HashSet<>(nGram.extractNgrams(document));
                            uniqueTerms.forEach(term -> documentFrequencies.merge(term, 1, Integer::sum));
                        }, executor))
                        .toArray(CompletableFuture[]::new))
                .join();

        return documentFrequencies;
    }

    private void computeIdfValues(Map<String, Integer> documentFrequencies, int numDocuments) {
        documentFrequencies.forEach((term, docFrequency) -> {
            var idf = Math.log((double) (numDocuments + 1) / (1 + docFrequency));
            idfValues.put(term, idf);
            System.out.println("Term: " + term + ", Doc Frequency: " + docFrequency + ", IDF: " + idf);
        });
    }

    private List<Map<String, Double>> computeTfidfVectors(List<String> documents) {
        var futures = documents.stream()
                .map(document -> CompletableFuture.supplyAsync(() -> {
                    var terms = nGram.extractNgrams(document);
                    var termFrequencies = computeTermFrequencies(terms);
                    Map<String, Double> tfidfValues = new HashMap<>(termFrequencies.size());

                    termFrequencies.forEach((term, tf) -> {
                        double idf = idfValues.getOrDefault(term, 0.0);
                        tfidfValues.put(term, tf * idf);
                    });

                    return tfidfValues;
                }, executor))
                .collect(Collectors.toList());

        var allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        ).join();
    }

    private Map<String, Double> computeTermFrequencies(List<String> terms) {
        Map<String, Double> termFrequencies = new HashMap<>(terms.size());
        terms.forEach(term -> termFrequencies.put(term, termFrequencies.getOrDefault(term, 0.0) + 1));
        termFrequencies.replaceAll((term, count) -> count / terms.size());
        return termFrequencies;
    }

    private DataFrame createTransformedDataFrame(DataFrame df, List<Map<String, Double>> tfidfVectors) {
        Map<String, AbstractSeries<?>> newData = new LinkedHashMap<>(df.getData());
        newData.remove(columnName);
        var allTerms = idfValues.keySet();

        allTerms.forEach(term -> {
            var termTfidfValues = tfidfVectors.stream()
                    .map(vector -> vector.getOrDefault(term, 0.0))
                    .collect(Collectors.toList());
            newData.put(term, new DoubleSeries(termTfidfValues));
        });

        return new DataFrame(newData);
    }
}