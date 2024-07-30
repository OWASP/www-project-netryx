package org.owasp.netryx.mlcore.frame.loader;

import org.owasp.netryx.mlcore.frame.series.AbstractSeries;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.frame.series.Series;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVLoader implements DataFrameLoader {

    @Override
    public DataFrame load(String filePath) throws IOException {
        try (var reader = new BufferedReader(new FileReader(filePath))) {
            var line = reader.readLine();

            if (line == null) {
                throw new IOException("Empty file");
            }

            var headers = line.split(",");
            Map<String, List<Object>> dataMap = new LinkedHashMap<>();
            for (var header : headers) {
                dataMap.put(header, new ArrayList<>());
            }

            while ((line = reader.readLine()) != null) {
                var values = line.split(",");
                if (values.length != headers.length) {
                    throw new IOException("Mismatch between number of values and headers in line: " + line);
                }
                for (var i = 0; i < values.length; i++) {
                    dataMap.get(headers[i]).add(parseValue(values[i]));
                }
            }

            Map<String, AbstractSeries<?>> seriesMap = new LinkedHashMap<>();
            for (var entry : dataMap.entrySet()) {
                seriesMap.put(entry.getKey(), new Series<>(entry.getValue()));
            }

            return new DataFrame(seriesMap);
        }
    }

    private Object parseValue(String value) {
        value = value.trim();
        if (value.matches("-?\\d+")) {
            return Integer.parseInt(value);
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
            return Double.parseDouble(value);
        } else {
            return value;
        }
    }
}