package org.loudsheep.psio_project.backend.observers;

import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.models.Transaction;

public interface SimulationResultObserver {
    void onStrategyResultUpdate(SimulationResult result);
    void onTransactionAdd(Transaction transaction);
}
