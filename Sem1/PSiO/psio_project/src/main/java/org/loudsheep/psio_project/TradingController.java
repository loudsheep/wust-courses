package org.loudsheep.psio_project;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.services.SaveFormulaService;
import org.loudsheep.psio_project.backend.services.StockService;
import org.loudsheep.psio_project.backend.trading.TradingFormulaValidator;
import org.loudsheep.psio_project.backend.trading.TradingFormula;
import org.loudsheep.psio_project.backend.trading.formulas.MACDTradingFormula;
import org.loudsheep.psio_project.backend.trading.validators.*;

import java.util.Map;

public class TradingController implements StockDataObserver {
    private static TradingController instance;

    // services and instances of StockData and
    private final StockService stockService;
    private StockData stockData;
    private TradingFormula tradingFormulaInstance;
    private Thread simulationThread;
    private double simulationBudget;

    private TradingController() {
        this.stockService = new StockService();
        this.stockService.addObserver(this);
    }

    // check if all instance are initialized and ready to execute the simulation
    public boolean isReadyToExecute() {
        if (this.tradingFormulaInstance == null || !this.tradingFormulaInstance.isReadyToExecute()) return false;
        if (this.simulationBudget <= 0) return false;
        return this.stockData != null;
    }

    // execute the simulation
    public void execute() {
        if (!this.isReadyToExecute()) return;
        this.stopExecution();

        // execute trading simulation async in new thread
        this.simulationThread = new Thread(() -> this.tradingFormulaInstance.execute(this.stockData, this.simulationBudget));
        this.simulationThread.start();
    }

    // halt execution of the simulation
    public void stopExecution() {
        if (this.tradingFormulaInstance != null) {
            this.tradingFormulaInstance.stopExecution();
        }
        if (this.simulationThread != null) {
            try {
                this.simulationThread.join();
            } catch (InterruptedException e) {
                return;
            }
            this.simulationThread = null;
        }
    }

    // save current method to file
    public boolean saveCurrentMethodToFile(String name) {
        if (this.tradingFormulaInstance == null) return false;

        return SaveFormulaService.saveTradingFormulaToFile(this.tradingFormulaInstance, name);
    }

    // receive new stock data
    @Override
    public void onDataChanged(StockData data) {
        this.stockData = data;
    }

    // handle stock service errors
    @Override
    public void setError(String error) {
    }

    // fetch stock data using service
    public void setStockData(String symbol, long startTime, long endTime) {
        // use StockService to fetch data (async)
        new Thread(() -> stockService.getStockData(symbol, startTime, endTime)).start();
    }

    public StockData getStockData() {
        return this.stockData;
    }

    // initialize new method with given params, create validator, and return errors if occurred
    public String[] setMethod(String methodName, Map<String, Object> params) {
        TradingFormulaValidator validator;

        switch (methodName) {
            case "SimpleUpAndDown" -> validator = new SimpleUpAndDownTradingFormulaValidator();
            case "Random" -> validator = new RandomTradingFormulaValidator();
            case "MACD" -> validator = new MACDTradingFormulaValidator();
            case "RSI" -> validator = new RSITradingFormulaValidator();
            default -> {
                return new String[]{"Unknown strategy name '" + methodName + "'"};
            }
        }

        String[] errors = validator.validate(params);
        if (errors.length > 0) return errors;

        this.tradingFormulaInstance = validator.create(params);
        return new String[0];
    }

    public TradingFormula getTradingFormulaInstance() {
        return this.tradingFormulaInstance;
    }

    public void addStockDataObserver(StockDataObserver observer) {
        this.stockService.addObserver(observer);
    }

    public void removeStockDataObserver(StockDataObserver observer) {
        this.stockService.removeObserver(observer);
    }

    public double getSimulationBudget() {
        return simulationBudget;
    }

    public void setSimulationBudget(double simulationBudget) {
        this.simulationBudget = Math.max(simulationBudget, 0.0);
    }

    public static TradingController getInstance() {
        if (instance == null) {
            instance = new TradingController();
        }
        return instance;
    }
}
