package org.owasp.netryx.mlcore.encoder.tfidf;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NGram {
    private final int minNgram;
    private final int maxNgram;
    private final Pattern tokenPattern;

    public NGram(int minNgram, int maxNgram, String tokenPattern) {
        this.minNgram = minNgram;
        this.maxNgram = maxNgram;
        this.tokenPattern = Pattern.compile(tokenPattern);
    }

    public List<String> extractNgrams(String document) {
        List<String> ngrams = new ArrayList<>();

        var matcher = tokenPattern.matcher(document);
        List<String> tokens = new ArrayList<>();

        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        for (var n = minNgram; n <= maxNgram; n++) {
            for (var i = 0; i <= tokens.size() - n; i++) {
                ngrams.add(String.join(" ", tokens.subList(i, i + n)));
            }
        }

        return ngrams;
    }

    public static NGram create(int minNgram, int maxNgram, String tokenPattern) {
        return new NGram(minNgram, maxNgram, tokenPattern);
    }
}