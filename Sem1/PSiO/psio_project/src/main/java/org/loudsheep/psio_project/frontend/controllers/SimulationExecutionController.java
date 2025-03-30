package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javafx.scene.shape.Line;
import org.loudsheep.psio_project.App;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.models.SimulationResult;
import org.loudsheep.psio_project.backend.models.Transaction;
import org.loudsheep.psio_project.backend.observers.SimulationResultObserver;
import org.loudsheep.psio_project.TradingController;
import org.loudsheep.psio_project.backend.trading.TradingFormula;
import org.loudsheep.psio_project.frontend.SceneManager;
import org.loudsheep.psio_project.frontend.util.DateStringConverter;
import org.loudsheep.psio_project.frontend.util.Epoch;

import java.util.Date;
import java.util.Optional;

public class SimulationExecutionController implements SimulationResultObserver {
    public Label strategyNameLabel;
    public Label strategyDescriptionLabel;
    public Label stockSymbolLabel;
    public Label stockDataRangeLabel;
    public Label stockDataPointsLabel;
    public Pane chartPane;
    public Label roiLabel;
    public Label transactionsLabel;
    public Label stockIncreaseLabel;
    public Label budgetIncreaseLabel;

    private XYChart.Series<Number, Number> buyTransactionSeries; // Green points
    private XYChart.Series<Number, Number> sellTransactionSeries;

    public void initialize() {
        StockData data = TradingController.getInstance().getStockData();
        TradingFormula strategy = TradingController.getInstance().getTradingFormulaInstance();

        this.strategyNameLabel.setText(strategy.getName());
        this.strategyDescriptionLabel.setText(strategy.getDescription());

        this.stockSymbolLabel.setText(data.symbol().toUpperCase());
        this.stockDataPointsLabel.setText(data.dailyData().size() + "");

        Date start = Epoch.toDate(data.getFirstDataPointTimestamp() * 1000);
        Date end = Epoch.toDate(data.getLastDataPointTimestamp() * 1000);
        this.stockDataRangeLabel.setText(start + " - " + end);

        double increase = (double) Math.round((data.getLastDataPoint().close() - data.getFirstDataPoint().close()) / data.getFirstDataPoint().close() * 100 * 100) / 100;
        this.stockIncreaseLabel.setText("Stock increase: " + increase + "%");

        this.createChart(data);
//        chart.prefWidthProperty().bind(this.chartPane.widthProperty());
//        chart.prefHeightProperty().bind(this.chartPane.heightProperty());
//
//        this.chartPane.getChildren().add(chart);

        TradingController.getInstance().getTradingFormulaInstance().addStrategyResultObserver(this);
    }

    private void createChart(StockData stockData) {
        // Axes
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");
        xAxis.setForceZeroInRange(false);
        xAxis.setTickLabelRotation(90);

        xAxis.setTickLabelFormatter(new DateStringConverter());

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Price");
        yAxis.setForceZeroInRange(false);
        yAxis.setVisible(false);

        // LineChart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Trading Chart");
        lineChart.setAnimated(false);
        lineChart.getStylesheets().add(App.class.getResource("styles/chart-styles.css").toExternalForm());
        lineChart.setLegendVisible(false);

        // Dataset 1 (Line chart)
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName("Stock Price");

        for (DayStockData day : stockData.dailyData()) {
            series1.getData().add(new XYChart.Data<>(day.timestamp(), day.close()));
        }

        for (XYChart.Data<Number, Number> data : series1.getData()) {
            Circle symbol = new Circle(1); // Set the radius of the circle (size)
            symbol.setFill(Color.BLUE); // Set the color of the symbol
            data.setNode(symbol); // Set the custom node as the symbol

        }

        // Dataset 2 (Green points)
        buyTransactionSeries = new XYChart.Series<>();
        buyTransactionSeries.setName("Buy transactions");

        // Dataset 3 (Red points)
        sellTransactionSeries = new XYChart.Series<>();
        sellTransactionSeries.setName("Sell transactions");

        // Add series to chart
        lineChart.getData().addAll(series1, buyTransactionSeries, sellTransactionSeries);

        buyTransactionSeries.getNode().setStyle("-fx-stroke: transparent;");
        sellTransactionSeries.getNode().setStyle("-fx-stroke: transparent;");
        buyTransactionSeries.getData().forEach(data ->
                data.getNode().setStyle("-fx-background-color: green, white; -fx-background-radius: 5px;"));
        sellTransactionSeries.getData().forEach(data ->
                data.getNode().setStyle("-fx-background-color: red, white; -fx-background-radius: 5px;"));

        // Tooltip for the closest data point
        Tooltip tooltip = new Tooltip();
        Tooltip.install(this.chartPane, tooltip);

        // Create a vertical line that follows the mouse
        Line verticalLine = new Line();
        verticalLine.setStroke(Color.GRAY);
        verticalLine.setStrokeWidth(1);
        verticalLine.setVisible(false);

        lineChart.prefWidthProperty().bind(this.chartPane.widthProperty());
        lineChart.prefHeightProperty().bind(this.chartPane.heightProperty());

        Pane pane = new Pane();
//        this.chartPane.getChildren().add(pane);
        this.chartPane.getChildren().add(lineChart);

        this.chartPane.getChildren().add(verticalLine);

        // Add the vertical line to the Pane
//        pane.getChildren().add(verticalLine);

        // Mouse moved event listener
        lineChart.setOnMouseMoved(event -> {
            double x = xAxis.getDisplayPosition(stockData.getFirstDataPointTimestamp());
            double y = yAxis.getDisplayPosition(stockData.getFirstDataPoint().close());
            double firstPointPosition = xAxis.localToScene(x, y).getX();

            // Get the mouse's x-coordinate in the scene
            double mouseXInScene = event.getSceneX();

            // Get the chart's position relative to the scene
            double chartXInScene = this.chartPane.getLayoutX();

            double mouseCorrection = firstPointPosition - x - chartXInScene - xAxis.getLayoutX();

            // Calculate the mouse's x position relative to the LineChart
            double mouseXRelativeToChart = mouseXInScene - chartXInScene - yAxis.getLayoutX() - yAxis.getLayoutBounds().getWidth() - mouseCorrection;

            // Adjust vertical line to follow mouse position
            verticalLine.setVisible(true);
            verticalLine.setStartX(mouseXInScene - chartXInScene);
            verticalLine.setEndX(mouseXInScene - chartXInScene);
            verticalLine.setStartY(0);
            verticalLine.setEndY(lineChart.getHeight());

            // Find the closest data point based on the mouse's x position
            XYChart.Data<Number, Number> closestData = getClosestDataPoint(series1, mouseXRelativeToChart, xAxis);

            if (closestData != null) {
                // Show tooltip near the vertical line with the closest data value
                double tooltipX = mouseXRelativeToChart + 10; // Adjust the tooltip X position slightly to the right
                double tooltipY = Math.round(closestData.getYValue().doubleValue() * 100) / 100.0;
                tooltip.setText("Price: " + tooltipY + "\n" + DateStringConverter.timestampToDateString(closestData.getXValue().longValue()));
//                tooltip.setX(tooltipX + SceneManager.getStage().getX());
//                tooltip.setY(yAxis.getDisplayPosition(tooltipY));
                tooltip.setX(event.getScreenX());
                tooltip.setY(event.getScreenY() + 10);
            }
        });

        // Hide the vertical line when the mouse is no longer over the chart
//        lineChart.setOnMouseExited(event -> {
//            verticalLine.setVisible(false);
//        });
    }

    private XYChart.Data<Number, Number> getClosestDataPoint(XYChart.Series<Number, Number> series, double mouseX, NumberAxis xAxis) {
        double closestDistance = Double.MAX_VALUE;
        XYChart.Data<Number, Number> closestData = null;

        for (XYChart.Data<Number, Number> data : series.getData()) {
            // Get the x position of the data point
            double dataX = xAxis.getDisplayPosition(data.getXValue());
            double distance = Math.abs(dataX - mouseX);

            // Track the closest data point
            if (distance < closestDistance) {
                closestDistance = distance;
                closestData = data;
            }
        }

        return closestData;
    }

    @Override
    public void onStrategyResultUpdate(SimulationResult result) {
        double roi = (double) Math.round(result.getROI() * 100 * 1000) / 1000;
        double budgetIncrease = (double) Math.round((result.getCurrentBudget() - result.getInitialBudget()) / result.getInitialBudget() * 100 * 100) / 100;

        Platform.runLater(() -> {
            this.roiLabel.setText("ROI: " + roi + "%");
            this.transactionsLabel.setText("No. of transactions: " + result.getNumberOfTransactions());
            this.budgetIncreaseLabel.setText("Budget increase: " + budgetIncrease + "%");
        });
    }

    @Override
    public void onTransactionAdd(Transaction transaction) {
        Platform.runLater(() -> {
            boolean isBuy = transaction.volume() < 0;

            if (isBuy) {
                XYChart.Data<Number, Number> data = new XYChart.Data<>(transaction.timestamp(), transaction.price());
                Circle symbol = new Circle(5); // Set the radius of the circle (size)
                symbol.setFill(Color.LIGHTGREEN); // Set the color of the symbol
                data.setNode(symbol);

                this.buyTransactionSeries.getData().add(data);
            } else {
                XYChart.Data<Number, Number> data = new XYChart.Data<>(transaction.timestamp(), transaction.price());
                Circle symbol = new Circle(5); // Set the radius of the circle (size)
                symbol.setFill(Color.RED); // Set the color of the symbol
                data.setNode(symbol);

                this.sellTransactionSeries.getData().add(data);
            }

        });
    }

    public void handleExecuteButtonClick(ActionEvent actionEvent) {
        this.buyTransactionSeries.getData().clear();
        this.sellTransactionSeries.getData().clear();
        TradingController.getInstance().execute();
    }

    public void handleStopTradingButton() {
        TradingController.getInstance().stopExecution();
    }

    public void handleBackToSelection() {
        TradingController.getInstance().stopExecution();

        SceneManager.switchScene("views/formula-select-view.fxml", "Select Formula");
    }

    public void handleMethodSave() {
        TextInputDialog dialog = new TextInputDialog("formula");
        dialog.setTitle("Enter name");
        dialog.setHeaderText("Give your formula a name");
        dialog.setContentText("Please enter name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (TradingController.getInstance().saveCurrentMethodToFile(name)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Saved successfully", ButtonType.OK);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not save", ButtonType.OK);
                alert.showAndWait();
            }
        });
    }
}