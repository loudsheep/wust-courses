package org.loudsheep.psio_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.loudsheep.psio_project.frontend.SceneManager;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setStage(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/formula-select-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Stock Simulation");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        TradingController.getInstance().stopExecution();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}