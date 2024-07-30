package org.owasp.netryx.mlcore.test.encoder;

import org.owasp.netryx.mlcore.encoder.OneHotEncoder;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.DoubleSeries;
import org.owasp.netryx.mlcore.frame.series.Series;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OneHotEncoderTest {

    @Test
    public void testFitAndTransform() {
        DataFrame df = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0)),
                "label", new Series<>(Arrays.asList("cat", "dog", "cat"))
        ));

        OneHotEncoder encoder = new OneHotEncoder();
        encoder.fit(df, "label");

        DataFrame transformedDf = encoder.transform(df);

        List<Double> expectedCatColumn = Arrays.asList(1.0, 0.0, 1.0);
        List<Double> expectedDogColumn = Arrays.asList(0.0, 1.0, 0.0);

        List<Double> transformedCatColumn = ((DoubleSeries) transformedDf.getColumn("label_cat")).getData();
        List<Double> transformedDogColumn = ((DoubleSeries) transformedDf.getColumn("label_dog")).getData();

        assertEquals(expectedCatColumn, transformedCatColumn);
        assertEquals(expectedDogColumn, transformedDogColumn);
    }

    @Test
    public void testFitWithMultipleUniqueValues() {
        DataFrame df = new DataFrame(Map.of(
                "label", new Series<>(Arrays.asList("cat", "dog", "fish", "cat", "dog", "fish"))
        ));

        OneHotEncoder encoder = new OneHotEncoder();
        encoder.fit(df, "label");

        assertNotNull(encoder.getUniqueValues());
        assertEquals(3, encoder.getUniqueValues().size());
        assertTrue(encoder.getUniqueValues().contains("cat"));
        assertTrue(encoder.getUniqueValues().contains("dog"));
        assertTrue(encoder.getUniqueValues().contains("fish"));
    }

    @Test
    public void testTransformWithoutFit() {
        DataFrame df = new DataFrame(Map.of(
                "feature1", new DoubleSeries(Arrays.asList(1.0, 2.0, 3.0)),
                "label", new Series<>(Arrays.asList("cat", "dog", "cat"))
        ));

        OneHotEncoder encoder = new OneHotEncoder();

        assertThrows(NullPointerException.class, () -> encoder.transform(df));
    }

    @Test
    public void testFitAndTransformWithNewData() {
        DataFrame df = new DataFrame(Map.of(
                "label", new Series<>(Arrays.asList("cat", "dog", "fish"))
        ));
        DataFrame newDf = new DataFrame(Map.of(
                "label", new Series<>(Arrays.asList("dog", "cat", "fish", "dog"))
        ));

        OneHotEncoder encoder = new OneHotEncoder();
        encoder.fit(df, "label");

        DataFrame transformedDf = encoder.transform(newDf);

        List<Double> expectedCatColumn = Arrays.asList(0.0, 1.0, 0.0, 0.0);
        List<Double> expectedDogColumn = Arrays.asList(1.0, 0.0, 0.0, 1.0);
        List<Double> expectedFishColumn = Arrays.asList(0.0, 0.0, 1.0, 0.0);

        List<Double> transformedCatColumn = ((DoubleSeries) transformedDf.getColumn("label_cat")).getData();
        List<Double> transformedDogColumn = ((DoubleSeries) transformedDf.getColumn("label_dog")).getData();
        List<Double> transformedFishColumn = ((DoubleSeries) transformedDf.getColumn("label_fish")).getData();

        assertEquals(expectedCatColumn, transformedCatColumn);
        assertEquals(expectedDogColumn, transformedDogColumn);
        assertEquals(expectedFishColumn, transformedFishColumn);
    }

    @Test
    public void testFitAndTransformWithEmptyData() {
        DataFrame df = new DataFrame(Map.of(
                "label", new Series<>(List.of())
        ));

        OneHotEncoder encoder = new OneHotEncoder();
        encoder.fit(df, "label");

        DataFrame transformedDf = encoder.transform(df);

        assertTrue(transformedDf.getColumns().isEmpty());
    }
}