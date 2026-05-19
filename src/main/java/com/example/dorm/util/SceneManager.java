package com.example.dorm.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource("/com/example/dorm/" + fxmlFile)
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, 1100, 680);
            java.net.URL css = SceneManager.class.getResource("/com/example/dorm/dark-theme.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
