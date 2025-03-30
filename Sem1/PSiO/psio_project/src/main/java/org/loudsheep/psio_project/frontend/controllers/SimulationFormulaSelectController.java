package org.loudsheep.psio_project.frontend.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.loudsheep.psio_project.App;
import org.loudsheep.psio_project.backend.models.DayStockData;
import org.loudsheep.psio_project.backend.models.StockData;
import org.loudsheep.psio_project.backend.observers.StockDataObserver;
import org.loudsheep.psio_project.backend.services.SaveFormulaService;
import org.loudsheep.psio_project.TradingController;
import org.loudsheep.psio_project.backend.trading.TradingFormula;
import org.loudsheep.psio_project.frontend.SceneManager;
import org.loudsheep.psio_project.frontend.util.Epoch;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class SimulationFormulaSelectController implements StockDataObserver {
    public VBox parametersVBox;
    public VBox savedVbox;

    // Stock data fields
    public TextField symbolField;
    public DatePicker startDateField;
    public DatePicker endDateField;
    public Label errorLabel;
    public LineChart lineChart;

    // menu items of different methods
    public MenuButton formulaMenuButton;
    public MenuItem randomFormulaButton;
    public MenuItem simpleUDFormulaButton;
    public TextField simulationBudgetField;
    public MenuItem macdFormulaButton;
    public MenuItem rsiFormulaButton;

    // Handles getting stock data
    public void initialize() {
        TradingController.getInstance().addStockDataObserver(this);
        StockData data = TradingController.getInstance().getStockData();
        if (data != null) {
            this.onDataChanged(data);
            this.symbolField.setText(data.symbol().toUpperCase());


            this.startDateField.setValue(Instant.ofEpochSecond(data.getFirstDataPointTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate());
            this.endDateField.setValue(Instant.ofEpochSecond(data.getLastDataPointTimestamp()).atZone(ZoneId.systemDefault()).toLocalDate());
        }

        this.showFormulaParams();
        this.showSavedMethods();

        UnaryOperator<TextFormatter.Change> doubleFilter = change -> change.getControlNewText().matches("-?\\d*(\\.\\d*)?") ? change : null;
        this.simulationBudgetField.setTextFormatter(new TextFormatter<>(doubleFilter));
        this.simulationBudgetField.setText(String.valueOf(TradingController.getInstance().getSimulationBudget()));

        this.errorLabel.setText("");

        this.simulationBudgetField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) return;
            try {
                TradingController.getInstance().setSimulationBudget(Double.parseDouble(simulationBudgetField.getText()));
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect budget format", ButtonType.OK);
                alert.showAndWait();
            }
        });
    }

    // show selected method params
    private void showFormulaParams() {
        TradingFormula formula = TradingController.getInstance().getTradingFormulaInstance();
        if (formula != null) {
            this.formulaMenuButton.setText(formula.getName());

            Label tmp = new Label();
            tmp.setWrapText(true);

            String text = "";
            for (Map.Entry<String, Object> set : formula.getMethodParams().entrySet()) {
                text += set.getKey() + ": " + set.getValue() + "\n";
            }
            tmp.setText(text);

            Button editBtn = new Button("Edit");
            editBtn.setOnAction(e -> this.editCurrentFormula());

            parametersVBox.getChildren().clear();
            parametersVBox.getChildren().addAll(tmp, editBtn);
        }
    }

    private void editCurrentFormula() {
        if (TradingController.getInstance().getTradingFormulaInstance() == null) return;

        String formulaName = TradingController.getInstance().getTradingFormulaInstance().getSignature();
        this.loadFormForMethod(formulaName, TradingController.getInstance().getTradingFormulaInstance().getMethodParams());
    }

    // load saved formula
    private void loadFormula(Map<String, Object> formula) {
        String name = formula.get("formulaName").toString();
        String[] errors = TradingController.getInstance().setMethod(name, formula);
        if (errors.length > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + errors[0], ButtonType.OK);
            alert.showAndWait();
        } else {
            this.showFormulaParams();
        }
    }

    // delete save formula from file
    private void deleteFormula(Map<String, Object> formula) {
        SaveFormulaService.deleteSavedFormula(formula.get("name").toString());
        this.showSavedMethods();
    }

    // list of saved methods and load/delete buttons
    private void showSavedMethods() {
        List<Map<String, Object>> methods = SaveFormulaService.getSavedTradingFormulas();

        this.savedVbox.getChildren().clear();
        for (Map<String, Object> method : methods) {
            VBox box = new VBox();

            Button loadBtn = new Button("Load");
            loadBtn.setOnAction(e -> loadFormula(method));
            Button deleteBtn = new Button("Delete");
            deleteBtn.setOnAction(e -> deleteFormula(method));
            HBox hbox = new HBox(loadBtn, deleteBtn);


            StringBuilder text = new StringBuilder();
            for (Map.Entry<String, Object> set : method.entrySet()) {
                text.append(set.getKey()).append(": ").append(set.getValue()).append("\n");
            }

            Label label = new Label(text.toString());
            Separator sep = new Separator();
            box.getChildren().addAll(label, hbox, sep);

            this.savedVbox.getChildren().add(box);
        }
    }

    private void loadFormForMethod(String methodName, Map<String, Object> params) {
        switch (methodName) {
            case "SimpleUpAndDown" -> loadMethodForm("views/forms/simple-method-form.fxml", params);
            case "Random" -> loadMethodForm("views/forms/random-method-form.fxml", params);
            case "MACD" -> loadMethodForm("views/forms/macd-method-form.fxml", params);
            case "RSI" -> loadMethodForm("views/forms/rsi-method-form.fxml", params);
            case null, default -> {
            }
        }
    }

    // for handling method selection/forms with params
    @FXML
    private void handleSimpleMethod() {
        loadFormForMethod("SimpleUpAndDown", Map.of());
        formulaMenuButton.setText(simpleUDFormulaButton.getText());
    }

    @FXML
    public void handleRandomMethod() {
        loadFormForMethod("Random", Map.of());
        formulaMenuButton.setText(randomFormulaButton.getText());
    }

    @FXML
    public void handleMACDFormula() {
        loadFormForMethod("MACD", Map.of());
        formulaMenuButton.setText(macdFormulaButton.getText());
    }

    public void handleRSIFormula() {
        loadFormForMethod("RSI", Map.of());
        formulaMenuButton.setText(rsiFormulaButton.getText());
    }

    // load and show params form for specified method
    private void loadMethodForm(String fxmlPath, Map<String, Object> initParams) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Node formNode = loader.load();

            // Pass initialization parameters to the form controller
            FormControllerInterface formController = loader.getController();
            formController.setParams(initParams);
            formController.setSubmitCallback(this::handleFormSubmit);

            parametersVBox.getChildren().clear();
            parametersVBox.getChildren().add(formNode);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Exception while loading data", ButtonType.OK);
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    // method params form callback
    private void handleFormSubmit(Map<String, Object> formData) {
        showFormulaParams();
    }

    // set stock data
    public void handleStockData() {
        if (this.startDateField.getValue() == null || this.endDateField.getValue() == null || this.symbolField.getText().isEmpty()) {
            this.errorLabel.setText("All fields are required");
            return;
        }

        long startEpoch = Epoch.toEpochSeconds(startDateField.getValue().getYear(), startDateField.getValue().getMonthValue(), startDateField.getValue().getDayOfMonth());
        long endEpoch = Epoch.toEpochSeconds(endDateField.getValue().getYear(), endDateField.getValue().getMonthValue(), endDateField.getValue().getDayOfMonth());

        TradingController.getInstance().setStockData(symbolField.getText(), startEpoch, endEpoch);
        this.errorLabel.setText("");
    }

    // on stock data changed callback, show chart
    @Override
    public void onDataChanged(StockData data) {
        Platform.runLater(() -> {
            XYChart.Series series = new XYChart.Series();
            series.setName(symbolField.getText() + " chart");

            for (DayStockData day : data.dailyData()) {
                series.getData().add(new XYChart.Data<>(day.timestamp() + "", day.close()));
            }

            lineChart.getData().clear();
            lineChart.getData().add(series);
        });
    }

    // handle stock api errors
    @Override
    public void setError(String error) {
        Platform.runLater(() -> {
            this.errorLabel.setText(error);
        });
    }

    // check if method and stock data are set, and show execution view
    @FXML
    public void handleSaveButtonClick() {
        if (!TradingController.getInstance().isReadyToExecute()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Not enough data to execute the simulation", ButtonType.OK);
            alert.showAndWait();
        } else {
            SceneManager.switchScene("views/simulation-execution-view.fxml", "Execute Simulation");
        }
    }
}
