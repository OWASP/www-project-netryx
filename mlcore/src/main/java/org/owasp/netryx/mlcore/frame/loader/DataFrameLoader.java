package org.owasp.netryx.mlcore.frame.loader;

import org.owasp.netryx.mlcore.frame.DataFrame;

import java.io.IOException;

public interface DataFrameLoader {
    DataFrame load(String filePath) throws IOException;
}