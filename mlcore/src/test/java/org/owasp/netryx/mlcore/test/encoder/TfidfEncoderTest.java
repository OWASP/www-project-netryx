package org.owasp.netryx.mlcore.test.encoder;

import org.owasp.netryx.mlcore.encoder.tfidf.TfidfEncoder;
import org.owasp.netryx.mlcore.encoder.tfidf.NGram;
import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.Series;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TfidfEncoderTest {
    private TfidfEncoder encoder;
    private DataFrame df;

    @BeforeEach
    public void setUp() {
        var nGram = new NGram(1, 2, "\\w+");
        encoder = new TfidfEncoder(nGram, 4);

        Map<String, AbstractSeries<?>> data = new HashMap<>();
        data.put("text", new Series<>(List.of(
                "this is a test",
                "this test is a test",
                "another test case",
                "this is another test"
        )));

        df = new DataFrame(data);
    }

    @Test
    public void testFit() {
        encoder.fit(df, "text");

        var idfValues = encoder.getIdfValues();
        assertNotNull(idfValues);

        assertEquals(14, idfValues.size(), "Expected 14 terms");

        assertEquals(Math.log((4.0 + 1) / (1 + 3)), idfValues.get("this"), 1e-5);
        assertEquals(Math.log((4.0 + 1) / (1 + 4)), idfValues.get("test"), 1e-5);
        assertEquals(Math.log((4.0 + 1) / (1 + 2)), idfValues.get("another"), 1e-5);
        assertEquals(Math.log((4.0 + 1) / (1 + 2)), idfValues.get("this is"), 1e-5);
        assertEquals(Math.log((4.0 + 1) / (1 + 2)), idfValues.get("a test"), 1e-5);
    }
}