package org.loudsheep.psio_project.backend.trading.formulas;

import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;
import org.loudsheep.psio_project.backend.trading.TradingFormula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MACDTradingFormula implements TradingFormula {
    private static final String name = "MACD Formula";
    private static final String description = "Uses MACD to identify buy and sell signals.";

    private final int shortPeriod;
    private final int longPeriod;
    private final int signalPeriod;

    private boolean stopExecution = false;
    private final SimulationResult result;

    public MACDTradingFormula(int shortPeriod, int longPeriod, int signalPeriod) {
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
        this.signalPeriod = signalPeriod;
        this.result = new SimulationResult(0);
    }

    private List<Double> calculateEMA(List<Double> prices, int period) {
        List<Double> ema = new ArrayList<>();
        if (prices.isEmpty() || period <= 0) return ema;

        double multiplier = 2.0 / (period + 1);
        double prevEMA = prices.get(0);
        ema.add(prevEMA);

        for (int i = 1; i < prices.size(); i++) {
            double currentEMA = (prices.get(i) - prevEMA) * multiplier + prevEMA;
            ema.add(currentEMA);
            prevEMA = currentEMA;
        }
        return ema;
    }

    private double getLastMACD(List<Double> prices) {
        List<Double> shortEMA = calculateEMA(prices, this.shortPeriod);
        List<Double> longEMA = calculateEMA(prices, this.longPeriod);

        List<Double> macdLine = new ArrayList<>();
        for (int i = 0; i < Math.min(shortEMA.size(), longEMA.size()); i++) {
            macdLine.add(shortEMA.get(i) - longEMA.get(i));
        }

        List<Double> signalLine = calculateEMA(macdLine, this.signalPeriod);

        if (macdLine.size() == 0 || signalLine.size() == 0) return 0;

        return macdLine.get(macdLine.size() - 1) - signalLine.get(signalLine.size() - 1);
    }

    @Override
    public void execute(StockData data, double budget) {
        this.result.resetState(Math.max(budget, 0.0));
        this.stopExecution = false;

        List<Double> closingPrices = new ArrayList<>();

        for (int i = 0; i < data.dailyData().size(); i++) {
            DayStockData dayData = data.dailyData().get(i);
            double price = dayData.close();
            closingPrices.add(price);

            if (i >= Math.max(this.shortPeriod, this.longPeriod)) {
                double macdValue = getLastMACD(closingPrices);

                if (macdValue > 0) { // Buy signal
                    this.result.buyStock(this.result.maxStockToBuy(price), price, dayData.timestamp());
                } else { // Sell signal
                    this.result.sellAllStock(price, dayData.timestamp());
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
        return this.shortPeriod > 0 && this.longPeriod > 0 && this.signalPeriod > 0;
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
        return MACDTradingFormula.description;
    }

    @Override
    public String getName() {
        return MACDTradingFormula.name;
    }

    @Override
    public String getSignature() {
        return "MACD";
    }

    @Override
    public Map<String, Object> getMethodParams() {
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("shortPeriod", shortPeriod);
        result.put("longPeriod", longPeriod);
        result.put("signalPeriod", signalPeriod);
        return result;
    }
}
