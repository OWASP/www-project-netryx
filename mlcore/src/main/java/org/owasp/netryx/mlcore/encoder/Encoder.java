package org.owasp.netryx.mlcore.encoder;

import org.owasp.netryx.mlcore.frame.DataFrame;

public interface Encoder {
    void fit(DataFrame df, String columnName);

    DataFrame transform(DataFrame df);
}