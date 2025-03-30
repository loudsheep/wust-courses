package org.loudsheep.psio_project.backend.models;

import java.util.List;

public record StockData(String symbol, List<DayStockData> dailyData) {

    public long getFirstDataPointTimestamp() {
        if (!this.dailyData.isEmpty()) return this.dailyData.getFirst().timestamp();
        return 0;
    }

    public long getLastDataPointTimestamp() {
        if (!this.dailyData.isEmpty()) return this.dailyData.getLast().timestamp();
        return 0;
    }

    public DayStockData getFirstDataPoint() {
        return this.dailyData.getFirst();
    }

    public DayStockData getLastDataPoint() {
        return this.dailyData.getLast();
    }

    // get stock data before the specified day; assumes that dailyData is sorted and doesn't include threshold in result
    public List<DayStockData> getStockDataBefore(DayStockData threshold, int maxSize) {
        for (int i = 0; i < this.dailyData.size(); i++) {
            if (this.dailyData.get(i).timestamp() >= threshold.timestamp()) {
                return this.dailyData.subList(Math.max(0, i - maxSize), i);
            }
        }
        return this.dailyData;
    }

    public List<DayStockData> getStockDataBefore(DayStockData threshold) {
        return this.getStockDataBefore(threshold, Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return "StockData{" +
                "symbol='" + symbol + '\'' +
                ", dailyData=" + dailyData +
                '}';
    }
}
