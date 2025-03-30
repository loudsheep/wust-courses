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

public class RSIFormulaFormController implements FormControllerInterface, FormErrorCallback {

    public VBox VBoxPane;
    public TextField period;
    public TextField sellThreshold;
    public TextField buyThreshold;
    private Label errorLabel;

    private FormSubmitCallback submitCallback;

    @FXML
    private void initialize() {
        // restrict input to integers for periods and doubles for budget/multiplier
        UnaryOperator<TextFormatter.Change> integerFilter = change -> change.getControlNewText().matches("-?\\d*") ? change : null;

        period.setTextFormatter(new TextFormatter<>(integerFilter));
        sellThreshold.setTextFormatter(new TextFormatter<>(integerFilter));
        buyThreshold.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    @Override
    public void setParams(Map<String, Object> params) {
        if (params.containsKey("period")) {
            this.period.setText(params.get("period").toString());
        }
        if (params.containsKey("sellThreshold")) {
            this.sellThreshold.setText(params.get("sellThreshold").toString());
        }
        if (params.containsKey("buyThreshold")) {
            this.buyThreshold.setText(params.get("buyThreshold").toString());
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
            formData.put("period", NumberParser.parseIntOrNull(period.getText()));
            formData.put("sellThreshold", NumberParser.parseIntOrNull(sellThreshold.getText()));
            formData.put("buyThreshold", NumberParser.parseIntOrNull(buyThreshold.getText()));

            String[] errors = TradingController.getInstance().setMethod("RSI", formData);
            if (errors.length > 0) {
                this.setError(errors[0]);
                return;
            }

            submitCallback.onSubmit(formData);
        }
    }
}
