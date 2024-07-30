package org.owasp.netryx.mlcore;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.prediction.LabelPrediction;

import java.util.List;

public interface Regressor extends Model {
    List<LabelPrediction> predict(DataFrame x);
}