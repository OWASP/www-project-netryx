package org.owasp.netryx.mlcore.test.encoder;

import org.owasp.netryx.mlcore.encoder.LabelEncoder;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.frame.series.Series;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LabelEncoderTest {

    @Test
    public void testFitAndTransform() {
        var df = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0)),
                "label", Series.of("cat", "dog", "cat")
        ));

        var encoder = new LabelEncoder();
        encoder.fit(df, "label");

        var transformedDf = encoder.transform(df);

        var expectedLabels = Arrays.asList(0.0, 1.0, 0.0);
        var transformedLabels = ((DoubleSeries) transformedDf.getColumn("label")).getData();

        assertEquals(expectedLabels, transformedLabels);
    }

    @Test
    public void testFitWithMultipleUniqueValues() {
        var df = new DataFrame(Map.of(
                "label", new Series<>(Arrays.asList("cat", "dog", "fish", "cat", "dog", "fish"))
        ));

        var encoder = new LabelEncoder();
        encoder.fit(df, "label");

        assertNotNull(encoder.getLabelMapping());
        assertEquals(3, encoder.getLabelMapping().size());

        assertTrue(encoder.getLabelMapping().containsKey("cat"));
        assertTrue(encoder.getLabelMapping().containsKey("dog"));
        assertTrue(encoder.getLabelMapping().containsKey("fish"));
    }

    @Test
    public void testTransformWithoutFit() {
        var df = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0)),
                "label", new Series<>(Arrays.asList("cat", "dog", "cat"))
        ));

        var encoder = new LabelEncoder();
        assertThrows(NullPointerException.class, () -> encoder.transform(df));
    }

    @Test
    public void testFitAndTransformWithNewData() {
        var df = new DataFrame(Map.of(
                "label", new Series<>(Arrays.asList("cat", "dog", "fish"))
        ));
        var newDf = new DataFrame(Map.of(
                "label", new Series<>(Arrays.asList("dog", "cat", "fish", "dog"))
        ));

        var encoder = new LabelEncoder();
        encoder.fit(df, "label");

        var transformedDf = encoder.transform(newDf);

        var expectedLabels = Arrays.asList(1.0, 0.0, 2.0, 1.0);
        var transformedLabels = transformedDf.getColumn("label").castAsDouble().getData();

        assertEquals(expectedLabels, transformedLabels);
    }

    @Test
    public void testFitAndTransformWithEmptyData() {
        var df = new DataFrame(Map.of(
                "label", new Series<>(List.of())
        ));

        var encoder = new LabelEncoder();
        encoder.fit(df, "label");

        var transformedDf = encoder.transform(df);

        List<Double> expectedLabels = List.of();
        var transformedLabels = ((DoubleSeries) transformedDf.getColumn("label")).getData();

        assertEquals(expectedLabels, transformedLabels);
    }
}
