package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingFormula;
import org.loudsheep.psio_project.backend.trading.TradingFormulaValidator;
import org.loudsheep.psio_project.backend.trading.formulas.MACDTradingFormula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MACDTradingFormulaValidator implements TradingFormulaValidator {
    @Override
    public String[] validate(Map<String, Object> formData) {
        List<String> errors = new ArrayList<>();

        if (!formData.containsKey("shortPeriod") || !(formData.get("shortPeriod") instanceof Number)) {
            errors.add("Short EMA period is required and must be an integer.");
        } else {
            int shortPeriod = ((Number) formData.get("shortPeriod")).intValue();
            if (shortPeriod <= 0) {
                errors.add("Short EMA period must be greater than 0.");
            }
        }

        if (!formData.containsKey("longPeriod") || !(formData.get("longPeriod") instanceof Number)) {
            errors.add("Long EMA period is required and must be an integer.");
        } else {
            int longPeriod = ((Number) formData.get("longPeriod")).intValue();
            if (longPeriod <= 0) {
                errors.add("Long EMA period must be greater than 0.");
            }

            if (formData.containsKey("shortPeriod") && formData.get("shortPeriod") instanceof Number) {
                int shortPeriod = ((Number) formData.get("shortPeriod")).intValue();
                if (shortPeriod >= longPeriod) {
                    errors.add("Short EMA period must be less than Long EMA period.");
                }
            }
        }

        if (!formData.containsKey("signalPeriod") || !(formData.get("signalPeriod") instanceof Number)) {
            errors.add("Signal period is required and must be an integer.");
        } else {
            int signalPeriod = ((Number) formData.get("signalPeriod")).intValue();
            if (signalPeriod <= 0) {
                errors.add("Signal period must be greater than 0.");
            }
        }

        return errors.toArray(new String[0]);
    }

    @Override
    public TradingFormula create(Map<String, Object> formData) {
        return new MACDTradingFormula(
                ((Number) formData.get("shortPeriod")).intValue(),
                ((Number) formData.get("longPeriod")).intValue(),
                ((Number) formData.get("signalPeriod")).intValue()
        );
    }
}
