package org.loudsheep.psio_project.backend.trading;

import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;

import java.util.Map;

public interface TradingFormula {
    String getDescription();
    String getName();
    String getSignature();
    Map<String, Object> getMethodParams();

    void execute(StockData data, double budget);
    boolean isReadyToExecute();
    void stopExecution();

    void addStrategyResultObserver(SimulationResultObserver observer);
    void removeStrategyResultObserver(SimulationResultObserver observer);
}
