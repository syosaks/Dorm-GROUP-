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
                SceneManager.class.getResource("/com/example/dorm/view/" + fxmlFile)
            );
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root, 900, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
