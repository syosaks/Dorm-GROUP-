package com.example.dorm.controller;

import com.example.dorm.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ViewReportsController {
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private TextField dateFromField;
    @FXML private TextField dateToField;
    @FXML private Label summaryLabel;
    @FXML private TableView<?> reportTable;

    @FXML
    public void initialize() {
        reportTypeComboBox.getItems().addAll("Occupancy", "Revenue", "Reservations");
        reportTypeComboBox.setValue("Occupancy");
    }

    @FXML
    private void onGenerateButtonClick() {
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("DashboardView.fxml");
    }
}
