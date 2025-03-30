package org.loudsheep.psio_project.backend.trading;

import java.util.Map;

public interface TradingFormulaValidator {
    String[] validate(Map<String, Object> formData);
    TradingFormula create(Map<String, Object> formData);
}
