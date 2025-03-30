package org.loudsheep.psio_project.frontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.loudsheep.psio_project.App;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}