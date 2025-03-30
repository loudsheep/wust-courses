package org.loudsheep.psio_project.backend.models;

import java.util.Date;

public record DayStockData(long timestamp, double low, double high, double open, double close) {
    // convert timestamp to Date object
    public Date getDate() {
        return new Date(timestamp * 1000); // Convert seconds to milliseconds
    }

    @Override
    public String toString() {
        return "DayStockData{" +
                "timestamp=" + timestamp +
                ", low=" + low +
                ", high=" + high +
                ", open=" + open +
                ", close=" + close +
                '}';
    }
}
