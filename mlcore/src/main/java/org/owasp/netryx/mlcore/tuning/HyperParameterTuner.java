package org.owasp.netryx.mlcore.tuning;

import org.owasp.netryx.mlcore.Model;
import org.owasp.netryx.mlcore.frame.DataFrame;

import java.util.List;
import java.util.Map;

public interface HyperParameterTuner {
    void tune(Model model, DataFrame X, DataFrame y, Map<String, List<?>> paramGrid);
}