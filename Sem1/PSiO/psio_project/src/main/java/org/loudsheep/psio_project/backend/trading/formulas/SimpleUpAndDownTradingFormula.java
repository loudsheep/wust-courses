package org.loudsheep.psio_project.backend.trading.formulas;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;
import org.loudsheep.psio_project.backend.trading.TradingFormula;

import java.util.Map;

public class SimpleUpAndDownTradingFormula implements TradingFormula {
    private static final String name = "SimpleUpAndDown Formula";
    private static final String description = "Simple formula that sells when downward trend, and buys when upward";
    private boolean stopExecution = false;

    private final int daysBackToCheck;
    private final SimulationResult result;

    public SimpleUpAndDownTradingFormula(int daysBackToCheck) {
        this.daysBackToCheck = daysBackToCheck;
        this.result = new SimulationResult(0);
    }

    private int getLastDaysTrend(StockData data, int currentDayIdx, int daysBack) {
        int trend = 0;
        DayStockData currentData = data.dailyData().get(currentDayIdx);
        for (int i = currentDayIdx; i >= Math.max(0, currentDayIdx - daysBack); i--) {
            DayStockData dayData = data.dailyData().get(i);

            if (dayData.open() == currentData.open()) trend = 0;
            else trend = (dayData.open() - currentData.open() > 0) ? 1 : -1;
        }
        return trend;
    }

    @Override
    public void execute(StockData data, double budget) {
        this.result.resetState(Math.max(budget, 0.0));
        this.stopExecution = false;

        for (int i = 0; i < data.dailyData().size(); i++) {
            DayStockData dayData = data.dailyData().get(i);
            double price = dayData.open();

            int trend = this.getLastDaysTrend(data, i, this.daysBackToCheck);

            if (trend > 0) {
                this.result.buyStock(this.result.maxStockToBuy(price), price, dayData.timestamp());
            } else {
                this.result.sellAllStock(price, dayData.timestamp());
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException _) {
            }

            if (this.stopExecution) {
                this.result.sellAllStock(price, dayData.timestamp());
                break;
            }
        }

        this.result.sellAllStock(data.dailyData().getLast().close(), data.getLastDataPointTimestamp());
    }

    @Override
    public boolean isReadyToExecute() {
        if (this.daysBackToCheck <= 0) return false;
        return true;
    }

    @Override
    public void stopExecution() {
        this.stopExecution = true;
    }

    @Override
    public void addStrategyResultObserver(SimulationResultObserver observer) {
        this.result.addObserver(observer);
    }

    @Override
    public void removeStrategyResultObserver(SimulationResultObserver observer) {
        this.result.removeObserver(observer);
    }

    @Override
    public String getDescription() {
        return SimpleUpAndDownTradingFormula.description;
    }

    @Override
    public String getName() {
        return SimpleUpAndDownTradingFormula.name;
    }

    @Override
    public String getSignature() {
        return "SimpleUpAndDown";
    }

    @Override
    public Map<String, Object> getMethodParams() {
        Map<String, Object> result = new java.util.HashMap<>();

        result.put("daysBackToCheck", daysBackToCheck);

        return result;
    }
}
