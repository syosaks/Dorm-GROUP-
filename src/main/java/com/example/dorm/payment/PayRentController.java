package com.example.dorm.payment;

import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.auth.User;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PayRentController {
    @FXML private ComboBox<String> tenantComboBox;
    @FXML private TextField amountField;
    @FXML private TextField monthField;
    @FXML private TableView<Payment> paymentHistoryTable;
    @FXML private Label monthlyRateLabel;

    private final TenantDAO tenantDAO = new TenantDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private List<Tenant> tenantList;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        TableColumn<Payment, String> colDate   = (TableColumn<Payment, String>) paymentHistoryTable.getColumns().get(0);
        TableColumn<Payment, Double> colAmount = (TableColumn<Payment, Double>) paymentHistoryTable.getColumns().get(1);
        TableColumn<Payment, String> colMonth  = (TableColumn<Payment, String>) paymentHistoryTable.getColumns().get(2);
        TableColumn<Payment, String> colStatus = (TableColumn<Payment, String>) paymentHistoryTable.getColumns().get(3);

        colDate.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getPaymentDate()));
        colAmount.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getAmount()).asObject());
        colMonth.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getMonthCovered()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));

        User user = Session.getCurrentUser();

        if ("TENANT".equals(user.getRole())) {
            // TENANT: locked to their own profile — cannot pay for anyone else
            Tenant tenant = tenantDAO.getByUserId(user.getId());
            if (tenant != null) {
                tenantList = new ArrayList<>();
                tenantList.add(tenant);
                tenantComboBox.getItems().add(tenant.getName());
                tenantComboBox.setValue(tenant.getName());
                tenantComboBox.setDisable(true);

                if (tenant.getRoomId() > 0) {
                    Room room = roomDAO.getRoomById(tenant.getRoomId());
                    if (room != null) {
                        monthlyRateLabel.setText("Room " + room.getRoomNumber()
                            + "  |  Monthly Rate: \u20b1" + String.format("%.2f", room.getMonthlyRate()));
                        amountField.setText(String.format("%.2f", room.getMonthlyRate()));
                    }
                } else {
                    monthlyRateLabel.setText("No room assigned yet.");
                }
                loadPaymentHistory();
            } else {
                monthlyRateLabel.setText("No tenant profile found. Contact admin.");
                tenantComboBox.setDisable(true);
            }
        } else {
            // ADMIN: can record payment for any tenant
            loadAllTenants();
            tenantComboBox.setOnAction(e -> loadPaymentHistory());
        }
    }

    private void loadAllTenants() {
        tenantList = tenantDAO.getAllTenants();
        tenantComboBox.getItems().clear();
        for (Tenant t : tenantList) tenantComboBox.getItems().add(t.getName());
        if (!tenantComboBox.getItems().isEmpty()) {
            tenantComboBox.setValue(tenantComboBox.getItems().get(0));
            loadPaymentHistory();
        }
    }

    private void loadPaymentHistory() {
        int idx = tenantComboBox.getSelectionModel().getSelectedIndex();
        if (tenantList == null || idx < 0 || idx >= tenantList.size()) return;
        Tenant tenant = tenantList.get(idx);

        // For ADMIN: show the room rate of the selected tenant
        User user = Session.getCurrentUser();
        if ("ADMIN".equals(user.getRole()) && tenant.getRoomId() > 0) {
            Room room = roomDAO.getRoomById(tenant.getRoomId());
            if (room != null) {
                monthlyRateLabel.setText("Room " + room.getRoomNumber()
                    + "  |  Monthly Rate: \u20b1" + String.format("%.2f", room.getMonthlyRate()));
            } else {
                monthlyRateLabel.setText("No room assigned.");
            }
        }

        paymentHistoryTable.setItems(FXCollections.observableArrayList(
            paymentDAO.getByTenantId(tenant.getId())));
    }

    @FXML
    private void onPayButtonClick() {
        int idx = tenantComboBox.getSelectionModel().getSelectedIndex();
        if (tenantList == null || idx < 0) {
            showAlert("Please select a tenant.");
            return;
        }

        String amountStr = amountField.getText().trim();
        String month = monthField.getText().trim();

        if (amountStr.isEmpty() || month.isEmpty()) {
            showAlert("Please enter both amount and month covered.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            showAlert("Amount must be a valid number.");
            return;
        }

        if (amount <= 0) {
            showAlert("Amount must be greater than zero.");
            return;
        }

        Tenant tenant = tenantList.get(idx);
        Payment p = new Payment();
        p.setTenantId(tenant.getId());
        p.setAmount(amount);
        p.setPaymentDate(LocalDate.now().toString());
        p.setMonthCovered(month);
        p.setStatus("PAID");
        paymentDAO.add(p);

        showAlert("Payment of \u20b1" + String.format("%.2f", amount)
            + " recorded for " + tenant.getName() + ".");
        monthField.clear();
        loadPaymentHistory();
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("dashboard/DashboardView.fxml");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
