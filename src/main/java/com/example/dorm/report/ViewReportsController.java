package com.example.dorm.report;

import com.example.dorm.shared.BaseController;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class ViewReportsController extends BaseController {
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private TextField dateFromField;
    @FXML private TextField dateToField;
    @FXML private Label summaryLabel;
    @FXML private TableView<String[]> reportTable;

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        super.initialize();
        reportTypeComboBox.getItems().addAll("Occupancy", "Revenue", "Reservations");
        reportTypeComboBox.setValue("Occupancy");

        TableColumn<String[], String> colCategory = (TableColumn<String[], String>) reportTable.getColumns().get(0);
        TableColumn<String[], String> colCount    = (TableColumn<String[], String>) reportTable.getColumns().get(1);
        TableColumn<String[], String> colDetails  = (TableColumn<String[], String>) reportTable.getColumns().get(2);

        colCategory.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        colCount.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[1]));
        colDetails.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[2]));

        // Hint labels on date fields for Occupancy (not applicable)
        reportTypeComboBox.setOnAction(e -> {
            String selected = reportTypeComboBox.getValue();
            if ("Occupancy".equals(selected)) {
                // Occupancy does not use date range
                dateFromField.setDisable(true);
                dateToField.setDisable(true);
                dateFromField.clear();
                dateToField.clear();
                dateFromField.setPromptText("N/A for Occupancy");
                dateToField.setPromptText("N/A for Occupancy");
            } else {
                dateFromField.setDisable(false);
                dateToField.setDisable(false);
                dateFromField.setPromptText("YYYY-MM-DD");
                dateToField.setPromptText("YYYY-MM-DD");
            }
        });

        // Occupancy doesn't use dates — disable initially
        dateFromField.setDisable(true);
        dateToField.setDisable(true);
        dateFromField.setPromptText("N/A for Occupancy");
        dateToField.setPromptText("N/A for Occupancy");
    }

    @FXML
    private void onGenerateButtonClick() {
        String type    = reportTypeComboBox.getValue();
        String from    = dateFromField.getText().trim();
        String to      = dateToField.getText().trim();
        List<String[]> data;

        // Validate date range if provided
        if (!from.isEmpty() || !to.isEmpty()) {
            if (from.isEmpty() || to.isEmpty()) {
                summaryLabel.setText("Please enter BOTH From and To dates, or leave both empty.");
                summaryLabel.setStyle("-fx-text-fill: #C62828;");
                return;
            }
            if (!from.matches("\\d{4}-\\d{2}-\\d{2}") || !to.matches("\\d{4}-\\d{2}-\\d{2}")) {
                summaryLabel.setText("Date format must be YYYY-MM-DD (e.g. 2026-01-01).");
                summaryLabel.setStyle("-fx-text-fill: #C62828;");
                return;
            }
            if (from.compareTo(to) > 0) {
                summaryLabel.setText("From date must be before To date.");
                summaryLabel.setStyle("-fx-text-fill: #C62828;");
                return;
            }
        }

        summaryLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");
        String rangeNote = (!from.isEmpty()) ? "  |  Date Range: " + from + " → " + to : "  |  All Dates";

        if (type.equals("Occupancy")) {
            data = reportDAO.getRoomOccupancyReport();
            int total    = reportDAO.getTotalRooms();
            int occupied = reportDAO.getOccupiedRooms();
            int tenants  = reportDAO.getTotalTenants();
            summaryLabel.setText("Total Rooms: " + total
                + "  |  Occupied: " + occupied
                + "  |  Available: " + (total - occupied)
                + "  |  Tenants: " + tenants);
        } else if (type.equals("Revenue")) {
            data = reportDAO.getRevenueByMonth(from, to);
            double total;
            if (from.isEmpty()) {
                total = reportDAO.getTotalRevenue();
            } else {
                total = reportDAO.getTotalRevenueInRange(from, to);
            }
            summaryLabel.setText("Total Revenue: \u20b1" + String.format("%.2f", total) + rangeNote);
        } else if (type.equals("Reservations")) {
            data = reportDAO.getReservationStatusReport(from, to);
            int pending = reportDAO.getPendingReservations();
            summaryLabel.setText("Pending: " + pending + rangeNote);
        } else {
            data = new ArrayList<>();
            summaryLabel.setText("");
        }

        reportTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void onBackButtonClick() { SceneManager.switchTo("dashboard/DashboardView.fxml"); }
}
