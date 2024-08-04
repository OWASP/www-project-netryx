package org.owasp.netryx.mlcore.encoder;

import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.serialize.MLComponent;

public interface Encoder extends MLComponent {
    void fit(DataFrame df, String columnName);

    DataFrame transform(DataFrame df);
}