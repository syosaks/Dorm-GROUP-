package com.example.dorm.controller;

import com.example.dorm.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ApproveReservationController {
    @FXML private TableView<?> reservationsTable;

    @FXML
    public void initialize() {
    }

    @FXML
    private void onApproveButtonClick() {
    }

    @FXML
    private void onRejectButtonClick() {
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("DashboardView.fxml");
    }
}
