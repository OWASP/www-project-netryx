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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ARFFLoader implements DataFrameLoader {

    private static final String ATTRIBUTE_PREFIX = "@attribute";
    private static final String DATA_PREFIX = "@data";
    private static final String COMMENT_PREFIX = "%";

    @Override
    public DataFrame load(String filePath) throws IOException {
        try (var reader = new BufferedReader(new FileReader(filePath))) {
            List<String> attributes = new ArrayList<>();
            Map<String, List<Object>> dataMap = new LinkedHashMap<>();

            var dataSection = false;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith(COMMENT_PREFIX))
                    continue;

                if (dataSection) {
                    processDataRow(line, attributes, dataMap);
                } else {
                    if (line.toLowerCase().startsWith(ATTRIBUTE_PREFIX)) {
                        processAttributeLine(line, attributes, dataMap);
                    } else if (line.toLowerCase().startsWith(DATA_PREFIX)) {
                        dataSection = true;
                    }
                }
            }

            return createDataFrame(attributes, dataMap);
        }
    }

    private void processAttributeLine(String line, List<String> attributes, Map<String, List<Object>> dataMap) {
        var parts = line.split("\\s+", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid attribute definition: " + line);
        }

        var attributeName = parts[1].split("\\s+")[0];
        attributes.add(attributeName);
        dataMap.put(attributeName, new ArrayList<>());
    }

    private void processDataRow(String line, List<String> attributes, Map<String, List<Object>> dataMap) {
        var values = line.split(",");
        if (values.length != attributes.size()) {
            throw new IllegalArgumentException("Data row does not match attribute count: " + line);
        }

        for (var i = 0; i < values.length; i++) {
            var attribute = attributes.get(i);
            dataMap.get(attribute).add(parseValue(values[i]));
        }
    }

    private Object parseValue(String value) {
        value = value.replace("'", "").trim();

        if (value.matches("-?\\d+")) {
            return Integer.parseInt(value);
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
            return Double.parseDouble(value);
        } else {
            return value;
        }
    }

    private DataFrame createDataFrame(List<String> attributes, Map<String, List<Object>> dataMap) {
        Map<String, AbstractSeries<?>> seriesMap = new LinkedHashMap<>();
        for (var attribute : attributes) {
            seriesMap.put(attribute, new Series<>(dataMap.get(attribute)));
        }
        return new DataFrame(seriesMap);
    }
}