package org.owasp.netryx.mlcore.tuning;

import org.owasp.netryx.mlcore.params.HyperParameter;
import org.owasp.netryx.mlcore.Model;
import org.owasp.netryx.mlcore.frame.DataFrame;
import org.owasp.netryx.mlcore.metrics.EvaluationMetrics;
import org.owasp.netryx.mlcore.validator.CrossValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridSearchCV implements HyperParameterTuner {
    private final CrossValidator crossValidator;
    private final Map<String, Object> bestParams;
    private EvaluationMetrics bestMetrics;

    public GridSearchCV(int k) {
        this.crossValidator = new CrossValidator(k);
        this.bestParams = new HashMap<>();
        this.bestMetrics = null;
    }

    @Override
    public void tune(Model model, DataFrame X, DataFrame y, Map<String, List<?>> paramGrid) {
        var paramCombinations = generateParamCombinations(paramGrid);
        var hyperParameters = model.getHyperParameters();

        for (var params : paramCombinations) {
            setModelParams(hyperParameters, params);
            var metrics = crossValidator.crossValidate(model, X, y);

            if (bestMetrics == null || metrics.getF1Score() > bestMetrics.getF1Score()) {
                bestMetrics = metrics;
                bestParams.clear();
                bestParams.putAll(params);
            }
        }
    }

    private List<Map<String, Object>> generateParamCombinations(Map<String, List<?>> paramGrid) {
        List<Map<String, Object>> paramCombinations = new ArrayList<>();
        List<String> keys = new ArrayList<>(paramGrid.keySet());

        var indices = new int[keys.size()];

        while (indices[0] < paramGrid.get(keys.get(0)).size()) {
            Map<String, Object> combination = new HashMap<>();

            for (var i = 0; i < keys.size(); i++)
                combination.put(keys.get(i), paramGrid.get(keys.get(i)).get(indices[i]));

            paramCombinations.add(combination);
            incrementIndices(indices, paramGrid, keys.size() - 1);
        }

        return paramCombinations;
    }

    private void incrementIndices(int[] indices, Map<String, List<?>> paramGrid, int index) {
        if (index < 0) return;

        indices[index]++;

        List<String> keys = new ArrayList<>(paramGrid.keySet());
        if (indices[index] == paramGrid.get(keys.get(index)).size()) {
            indices[index] = 0;
            incrementIndices(indices, paramGrid, index - 1);
        }
    }

    private <T> void setModelParams(List<HyperParameter<?>> hyperParameters, Map<String, Object> params) {
        for (var hyperParameter : hyperParameters) {
            if (params.containsKey(hyperParameter.getName())) {
                setValue(hyperParameter, params.get(hyperParameter.getName()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void setValue(HyperParameter<T> hyperParameter, Object value) {
        hyperParameter.setValue((T) value);
    }

    public Map<String, Object> getBestParams() {
        return bestParams;
    }

    public EvaluationMetrics getBestMetrics() {
        return bestMetrics;
    }
}