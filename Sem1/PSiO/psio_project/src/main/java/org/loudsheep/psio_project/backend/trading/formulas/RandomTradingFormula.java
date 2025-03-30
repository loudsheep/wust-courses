package org.loudsheep.psio_project.backend.trading.formulas;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;
import org.loudsheep.psio_project.backend.trading.TradingFormula;

import java.util.Map;

public class RandomTradingFormula implements TradingFormula {
    private static final String name = "Random Formula";
    private static final String description = "Random decisions";

    private final SimulationResult result;
    private boolean stopExecution = false;

    public RandomTradingFormula() {
        this.result = new SimulationResult(0);
    }

    @Override
    public void execute(StockData data, double budget) {
        this.result.resetState(Math.max(budget, 0.0));
        this.stopExecution = false;

        for (int i = 0; i < data.dailyData().size(); i++) {
            DayStockData dayData = data.dailyData().get(i);
            double price = dayData.open();

            double rand = Math.random();
            // 10% -> buy Transaction
            // 10% -> sell Transaction
            // 80% -> no action at all

            if (rand < 0.1) {
                int maxToBuy = this.result.maxStockToBuy(price);
                int randomBuyAmount = (int) Math.floor(Math.random() * maxToBuy);

                this.result.buyStock(randomBuyAmount, price, dayData.timestamp());
            } else if (rand < 0.2) {
                int randomToSell = (int) Math.floor(this.result.getStockOwned() * Math.random());

                this.result.sellStock(randomToSell, price, dayData.timestamp());
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
        return RandomTradingFormula.description;
    }

    @Override
    public String getName() {
        return RandomTradingFormula.name;
    }

    @Override
    public String getSignature() {
        return "Random";
    }

    @Override
    public Map<String, Object> getMethodParams() {
        return new java.util.HashMap<>();
    }
}
