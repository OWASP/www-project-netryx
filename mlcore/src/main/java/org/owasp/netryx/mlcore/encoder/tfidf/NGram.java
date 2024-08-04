package org.owasp.netryx.mlcore.encoder.tfidf;

import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NGram implements MLComponent {
    private int minNgram;
    private int maxNgram;
    private Pattern tokenPattern;

    public NGram(int minNgram, int maxNgram, String tokenPattern) {
        this.minNgram = minNgram;
        this.maxNgram = maxNgram;
        this.tokenPattern = Pattern.compile(tokenPattern);
    }

    public NGram() {
        this(0, 0, "");
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

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(minNgram);
        out.writeInt(maxNgram);

        var pattern = tokenPattern.pattern().getBytes(StandardCharsets.UTF_8);
        out.writeInt(pattern.length);
        out.write(pattern);
    }

    @Override
    public void load(DataInputStream in) throws IOException {
        minNgram = in.readInt();
        maxNgram = in.readInt();

        var size = in.readInt();
        var bytes = new byte[size];
        var readBytes = in.read(bytes);

        if (readBytes != size)
            throw new IllegalArgumentException("Not a pattern: " + new String(bytes));

        this.tokenPattern = Pattern.compile(new String(bytes));
    }
}