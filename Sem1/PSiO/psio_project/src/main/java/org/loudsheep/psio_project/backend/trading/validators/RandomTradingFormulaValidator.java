package org.loudsheep.psio_project.backend.trading.validators;

import org.loudsheep.psio_project.backend.trading.TradingFormula;
import org.loudsheep.psio_project.backend.trading.TradingFormulaValidator;
import org.loudsheep.psio_project.backend.trading.formulas.RandomTradingFormula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RandomTradingFormulaValidator implements TradingFormulaValidator {
    @Override
    public String[] validate(Map<String, Object> formData) {
        return new String[0];
    }

    @Override
    public TradingFormula create(Map<String, Object> formData) {
        return new RandomTradingFormula();
    }
}
