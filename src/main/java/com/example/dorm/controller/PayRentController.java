package com.example.dorm.controller;

import com.example.dorm.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class PayRentController {
    @FXML private ComboBox<String> tenantComboBox;
    @FXML private TextField amountField;
    @FXML private TextField monthField;
    @FXML private TableView<?> paymentHistoryTable;

    @FXML
    public void initialize() {
    }

    @FXML
    private void onPayButtonClick() {
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("DashboardView.fxml");
    }
}
