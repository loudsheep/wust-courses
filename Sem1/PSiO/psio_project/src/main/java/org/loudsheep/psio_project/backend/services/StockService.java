package org.loudsheep.psio_project.backend.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StockService {
    private static final String BASE_URL = "https://query1.finance.yahoo.com/v8/finance/chart/%s?events=capitalGain|div|split&formatted=true&includeAdjustedClose=true&interval=1d&period1=%s&period2=%s";
    private final List<StockDataObserver> observers = new ArrayList<>();

    // register an observer
    public void addObserver(StockDataObserver observer) {
        observers.add(observer);
    }

    // remove an observer
    public void removeObserver(StockDataObserver observer) {
        observers.remove(observer);
    }

    // notify all observers
    private void notifyObservers(StockData data) {
        for (StockDataObserver observer : observers) {
            observer.onDataChanged(data);
        }
    }

    // notify observers if error occurred
    private void notifyError(String error) {
        for (StockDataObserver observer : observers) {
            observer.setError(error);
        }
    }

    // fetch and parse stock data for a given symbol
    public void getStockData(String symbol, long startTime, long endTime) {
        String urlString = String.format(BASE_URL, symbol.toUpperCase(), startTime, endTime);
        String jsonResponse;
        try {
            jsonResponse = fetchJsonData(urlString);

            // parse the fetched data
            StockData stockData = parseStockData(symbol, jsonResponse);

            // notify observers with the new stock data
            notifyObservers(stockData);
        } catch (IOException e) {
            // error occurred, notify with message
            this.notifyError(e.getMessage());
        }
    }

    // fetch JSON data from URL
    private String fetchJsonData(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // check the HTTP response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new IOException("Dates must not be in future!");
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new IOException("Symbol not found");
        } else if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch data, HTTP response code: " + responseCode);
        }

        // read response from input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    // parse JSON response to StockData
    private StockData parseStockData(String symbol, String jsonResponse) throws IOException {
        List<DayStockData> dailyData = new ArrayList<>();

        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject resultArray = jsonObject.getAsJsonObject("chart").getAsJsonArray("result")
                .get(0).getAsJsonObject();

        JsonObject quoteObject = resultArray.getAsJsonObject("indicators").getAsJsonArray("quote").get(0).getAsJsonObject();

        if (quoteObject.isEmpty()) throw new IOException("Data for this symbol does not exist");

        for (int i = 0; i < resultArray.getAsJsonArray("timestamp").size(); i++) {
            long timestamp = resultArray.getAsJsonArray("timestamp").get(i).getAsLong();
            JsonElement low = quoteObject.getAsJsonArray("low").get(i);
            JsonElement high = quoteObject.getAsJsonArray("high").get(i);
            JsonElement open = quoteObject.getAsJsonArray("open").get(i);
            JsonElement close = quoteObject.getAsJsonArray("close").get(i);

            if (low.isJsonNull() || high.isJsonNull() || open.isJsonNull() || close.isJsonNull()) {
                continue;
            }

            dailyData.add(new DayStockData(timestamp, low.getAsDouble(), high.getAsDouble(), open.getAsDouble(), close.getAsDouble()));
        }

        // create and return StockData object
        return new StockData(symbol, dailyData);
    }
}
