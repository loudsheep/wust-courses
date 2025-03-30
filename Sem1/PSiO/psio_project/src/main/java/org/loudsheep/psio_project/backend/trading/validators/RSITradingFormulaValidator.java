package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingFormula;
import org.loudsheep.psio_project.backend.trading.TradingFormulaValidator;
import org.loudsheep.psio_project.backend.trading.formulas.RSITradingFormula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RSITradingFormulaValidator implements TradingFormulaValidator {
    @Override
    public String[] validate(Map<String, Object> formData) {
        List<String> errors = new ArrayList<>();

        if (!formData.containsKey("period") || !(formData.get("period") instanceof Number)) {
            errors.add("Period is required and must be an integer.");
        } else {
            int period = ((Number) formData.get("period")).intValue();
            if (period <= 0) {
                errors.add("Period must be greater than 0.");
            }
        }

        if (!formData.containsKey("sellThreshold") || !(formData.get("sellThreshold") instanceof Number)) {
            errors.add("Sell threshold is required and must be an integer.");
        } else if (!formData.containsKey("buyThreshold") || !(formData.get("buyThreshold") instanceof Number)) {
            errors.add("Buy threshold is required and must be an integer.");
        } else {
            int sellThreshold = ((Number) formData.get("sellThreshold")).intValue();
            if (sellThreshold <= 0) {
                errors.add("Sell threshold must be greater than 0.");
            } else if (sellThreshold > 100) {
                errors.add("Sell threshold must be less than or equal 100.");
            }

            int buyThreshold = ((Number) formData.get("buyThreshold")).intValue();
            if (buyThreshold <= 0) {
                errors.add("Buy threshold must be greater than 0.");
            } else if (buyThreshold > 100) {
                errors.add("Buy threshold must be less than or equal 100.");
            }

            if (sellThreshold <= buyThreshold) {
                errors.add("Sell Threshold must be greater than Buy Threshold");
            }
        }

        return errors.toArray(new String[0]);
    }

    @Override
    public TradingFormula create(Map<String, Object> formData) {
        return new RSITradingFormula(
                ((Number) formData.get("period")).intValue(),
                ((Number) formData.get("sellThreshold")).intValue(),
                ((Number) formData.get("buyThreshold")).intValue()
        );
    }
}
