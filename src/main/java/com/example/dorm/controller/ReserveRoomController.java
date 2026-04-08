package com.example.dorm.controller;

import com.example.dorm.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ReserveRoomController {
    @FXML private TableView<?> roomsTable;

    @FXML
    public void initialize() {
    }

    @FXML
    private void onReserveButtonClick() {
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("DashboardView.fxml");
    }
}
