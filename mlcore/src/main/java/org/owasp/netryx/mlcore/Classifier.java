package org.owasp.netryx.mlcore;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.prediction.ClassificationPrediction;

import java.util.List;

public interface Classifier extends Model {
    List<ClassificationPrediction> predict(DataFrame x);
}
