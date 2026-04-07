package com.example.dorm.controller;

import com.example.dorm.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public class AssignRoomController {
    @FXML private ComboBox<String> tenantComboBox;
    @FXML private ComboBox<String> roomComboBox;
    @FXML private TableView<?> assignmentsTable;

    @FXML
    public void initialize() {
    }

    @FXML
    private void onAssignButtonClick() {
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("DashboardView.fxml");
    }
}
