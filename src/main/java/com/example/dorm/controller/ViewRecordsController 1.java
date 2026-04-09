package com.example.dorm.controller;

import com.example.dorm.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ViewRecordsController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<?> recordsTable;

    @FXML
    public void initialize() {
        filterComboBox.getItems().addAll("Tenants", "Rooms", "Payments");
        filterComboBox.setValue("Tenants");
    }

    @FXML
    private void onSearchButtonClick() {
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("DashboardView.fxml");
    }
}
