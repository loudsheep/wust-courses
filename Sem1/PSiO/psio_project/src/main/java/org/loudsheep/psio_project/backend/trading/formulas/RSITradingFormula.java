package org.loudsheep.psio_project.backend.trading.formulas;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;
import org.loudsheep.psio_project.backend.trading.TradingFormula;

import java.util.List;
import java.util.Map;

public class RSITradingFormula implements TradingFormula {
    private static final String name = "RSI Formula";
    private static final String description = "Uses Relative Strength Index and price thresholds";

    private final int period;
    private final int sellThreshold;
    private final int buyThreshold;

    private boolean stopExecution = false;
    private final SimulationResult result;

    public RSITradingFormula(int period, int sellThreshold, int buyThreshold) {
        this.period = period;
        this.sellThreshold = sellThreshold;
        this.buyThreshold = buyThreshold;
        this.result = new SimulationResult(0);
    }

    private double calculateRSI(List<DayStockData> data, int rsiPeriod) {
        double gains = 0;
        double losses = 0;
        for (int i = data.size() - 1; i >= Math.max(data.size() - rsiPeriod, 0) + 1; i--) {
            double change = data.get(i).close() - data.get(i - 1).close();

            if (change > 0) gains += change;
            else losses -= change;
        }

        double averageGain = gains / rsiPeriod;
        double averageLoss = losses / rsiPeriod;

        double rs = (averageLoss == 0) ? Double.MAX_VALUE : averageGain / averageLoss;
        return 100 - (100 / (1 + rs));
    }

    @Override
    public void execute(StockData data, double budget) {
        this.result.resetState(Math.max(budget, 0.0));
        this.stopExecution = false;
        
        for (int i = 0; i < data.dailyData().size(); i++) {
            DayStockData dayData = data.dailyData().get(i);
            double price = dayData.close();

            if (i >= this.period) {
                double rsi = this.calculateRSI(data.getStockDataBefore(dayData), this.period);

                if (rsi >= sellThreshold) {
                    this.result.sellAllStock(price, dayData.timestamp());
                } else if (rsi <= buyThreshold) {
                    this.result.buyStock(this.result.maxStockToBuy(price), price, dayData.timestamp());
                }
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
        return this.period > 0;
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
        return RSITradingFormula.description;
    }

    @Override
    public String getName() {
        return RSITradingFormula.name;
    }

    @Override
    public String getSignature() {
        return "RSI";
    }

    @Override
    public Map<String, Object> getMethodParams() {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("period", period);
        result.put("sellThreshold", sellThreshold);
        result.put("buyThreshold", buyThreshold);
        return result;
    }
}
