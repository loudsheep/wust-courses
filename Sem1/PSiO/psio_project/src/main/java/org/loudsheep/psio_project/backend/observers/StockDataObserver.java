package org.loudsheep.psio_project.backend.observers;

import org.loudsheep.psio_project.backend.models.StockData;

public interface StockDataObserver {
    void onDataChanged(StockData data);
    void setError(String error);
}
