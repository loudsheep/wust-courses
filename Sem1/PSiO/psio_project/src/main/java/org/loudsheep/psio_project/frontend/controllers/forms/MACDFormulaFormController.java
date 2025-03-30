package org.loudsheep.psio_project.frontend.controllers.forms;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.loudsheep.psio_project.TradingController;
import org.loudsheep.psio_project.backend.util.NumberParser;
import org.loudsheep.psio_project.frontend.controllers.FormControllerInterface;
import org.loudsheep.psio_project.frontend.interfaces.FormErrorCallback;
import org.loudsheep.psio_project.frontend.interfaces.FormSubmitCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class MACDFormulaFormController implements FormControllerInterface, FormErrorCallback {

    public VBox VBoxPane;
    public TextField shortEmaField;
    public TextField longEmaField;
    public TextField bollingerPeriodField;
    private Label errorLabel;

    private FormSubmitCallback submitCallback;

    @FXML
    private void initialize() {
        // restrict input to integers for periods and doubles for budget/multiplier
        UnaryOperator<TextFormatter.Change> integerFilter = change -> change.getControlNewText().matches("-?\\d*") ? change : null;
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> change.getControlNewText().matches("-?\\d*(\\.\\d*)?") ? change : null;

        shortEmaField.setTextFormatter(new TextFormatter<>(integerFilter));
        longEmaField.setTextFormatter(new TextFormatter<>(integerFilter));
        bollingerPeriodField.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params.containsKey("shortPeriod")) {
            this.shortEmaField.setText(params.get("shortPeriod").toString());
        }
        if (params.containsKey("longPeriod")) {
            this.longEmaField.setText(params.get("longPeriod").toString());
        }
        if (params.containsKey("signalPeriod")) {
            this.bollingerPeriodField.setText(params.get("signalPeriod").toString());
        }
    }

    @Override
    public void setSubmitCallback(FormSubmitCallback callback) {
        this.submitCallback = callback;
    }

    @Override
    public void setError(String text) {
        if (Objects.equals(text, "") && this.errorLabel != null) {
            this.VBoxPane.getChildren().remove(errorLabel);
            this.errorLabel = null;
            return;
        }

        if (this.errorLabel == null) {
            this.errorLabel = new Label(text);
            this.errorLabel.setTextFill(Color.RED);
            this.errorLabel.setWrapText(true);
            this.VBoxPane.getChildren().add(this.VBoxPane.getChildren().size() - 1, this.errorLabel);
        } else {
            this.errorLabel.setText(text);
        }
    }

    @FXML
    private void handleSubmit() throws Exception {
        if (submitCallback != null) {
            Map<String, Object> formData = new HashMap<>();
            formData.put("shortPeriod", NumberParser.parseIntOrNull(shortEmaField.getText()));
            formData.put("longPeriod", NumberParser.parseIntOrNull(longEmaField.getText()));
            formData.put("signalPeriod", NumberParser.parseIntOrNull(bollingerPeriodField.getText()));

            String[] errors = TradingController.getInstance().setMethod("MACD", formData);
            if (errors.length > 0) {
                this.setError(errors[0]);
                return;
            }

            submitCallback.onSubmit(formData);
        }
    }
}
