package com.example.dorm.controller;

import com.example.dorm.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome to the Dormitory Management System");
    }

    @FXML private void onReserveRoom() { SceneManager.switchTo("ReserveRoomView.fxml"); }
    @FXML private void onPayRent() { SceneManager.switchTo("PayRentView.fxml"); }
    @FXML private void onViewRecords() { SceneManager.switchTo("ViewRecordsView.fxml"); }
    @FXML private void onAssignRoom() { SceneManager.switchTo("AssignRoomView.fxml"); }
    @FXML private void onApproveReservation() { SceneManager.switchTo("ApproveReservationView.fxml"); }
    @FXML private void onViewReports() { SceneManager.switchTo("ViewReportsView.fxml"); }
    @FXML private void onLogout() { SceneManager.switchTo("LoginView.fxml"); }
}
