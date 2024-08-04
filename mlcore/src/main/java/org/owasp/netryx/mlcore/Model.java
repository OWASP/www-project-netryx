package org.owasp.netryx.mlcore;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.metrics.ModelEvaluator;
import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.prediction.Prediction;
import org.owasp.netryx.mlcore.serialize.MLComponent;

import java.util.List;

public interface Model extends MLComponent {
    void fit(DataFrame X, DataFrame y);

    List<? extends Prediction> predict(DataFrame x);

    List<HyperParameter<?>> getHyperParameters();

    default ModelEvaluator evaluator() {
        return new ModelEvaluator(this);
    }
}
