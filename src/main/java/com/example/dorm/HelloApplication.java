package com.example.dorm;

import com.example.dorm.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        stage.setTitle("DormLink — Dormitory Management System");
        SceneManager.switchTo("auth/LoginView.fxml");
        stage.show();
    }
}
