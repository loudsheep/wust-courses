package org.loudsheep.psio_project.backend.models;

import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;

import java.util.ArrayList;
import java.util.List;

public class SimulationResult {
    private final List<Transaction> transactions = new ArrayList<>();
    // initial money available
    private double initialBudget;
    // current money available
    private double currentBudget;
    private int stockOwned;
    private final List<SimulationResultObserver> observers = new ArrayList<>();

    public SimulationResult(double initialBudget) {
        this.initialBudget = Math.max(initialBudget, 0);
        this.currentBudget = this.initialBudget;
        this.stockOwned = 0;
    }

    // check if value of new transaction does not exceed current budget
    public boolean canAddTransaction(double value) {
        return this.currentBudget + value >= 0;
    }

    // add new transaction, where volume < 0 - buy, and volume > 0 - sell
    public void addTransaction(int volume, double price, long timestamp) {
        if (!this.canAddTransaction(volume * price)) return;
        if (volume == 0) return;

        Transaction newTransaction = new Transaction(volume, price, timestamp);
        this.transactions.add(newTransaction);
        this.currentBudget += volume * price;

        this.notifyObserversWithNewTransaction(newTransaction);
        this.notifyObserversWithObjectUpdate();
    }

    public boolean hasEnoughMoneyToBuy(int amount, double price) {
        return this.canAddTransaction(amount * price);
    }

    // get max number of stock that can be bought with given price per stock
    public int maxStockToBuy(double price) {
        return (int) Math.floor(this.currentBudget / price);
    }

    public void buyStock(int amount, double price, long timestamp) {
        if (!hasEnoughMoneyToBuy(-amount, price)) return;

        this.stockOwned += amount;
        // negative amount means buy
        this.addTransaction(-amount, price, timestamp);
    }

    public void sellStock(int amount, double price, long timestamp) {
        if (amount >= this.stockOwned) amount = this.stockOwned;

        this.stockOwned -= amount;
        // positive amount means sell
        this.addTransaction(amount, price, timestamp);

    }

    public void sellAllStock(double price, long timestamp) {
        this.addTransaction(this.stockOwned, price, timestamp);
        this.stockOwned = 0;
    }

    public int getNumberOfTransactions() {
        return this.transactions.size();
    }

    // return on investment
    public double getROI() {
        return this.currentBudget / this.initialBudget;
    }

    public double getCurrentBudget() {
        return currentBudget;
    }

    public int getStockOwned() {
        return stockOwned;
    }

    public void addObserver(SimulationResultObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(SimulationResultObserver observer) {
        this.observers.remove(observer);
    }

    private void notifyObserversWithNewTransaction(Transaction transaction) {
        for (SimulationResultObserver o : this.observers) {
            o.onTransactionAdd(transaction);
        }
    }

    private void notifyObserversWithObjectUpdate() {
        for (SimulationResultObserver o : this.observers) {
            o.onStrategyResultUpdate(this);
        }
    }

    public void resetState() {
        this.currentBudget = this.initialBudget;
        this.transactions.clear();
        this.stockOwned = 0;
    }

    public void resetState(double initialBudget) {
        this.initialBudget = Math.max(initialBudget, 0);
        this.resetState();
    }

    public double getInitialBudget() {
        return initialBudget;
    }

    @Override
    public String toString() {
        return "StrategyResult{" +
                "initialBudget=" + initialBudget +
                ", currentBudget=" + currentBudget +
                ", numOfTransactions=" + this.getNumberOfTransactions() +
                ", roi=" + this.getROI() +
                '}';
    }
}
