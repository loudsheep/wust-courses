package org.loudsheep.psio_project.backend.models;

import java.util.Date;

public record Transaction(int volume, double price, long timestamp) {

    public double getTotalValue() {
        return this.volume * this.price;
    }

    public Date getDate() {
        return new Date(this.timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return volume == that.volume && Double.compare(price, that.price) == 0 && timestamp == that.timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "volume=" + volume +
                ", price=" + price +
                ", timestamp=" + timestamp +
                '}';
    }
}
