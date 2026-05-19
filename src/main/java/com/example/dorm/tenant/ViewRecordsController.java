package com.example.dorm.tenant;

import com.example.dorm.payment.Payment;
import com.example.dorm.payment.PaymentDAO;
import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.shared.BaseController;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class ViewRecordsController extends BaseController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<String[]> recordsTable;

    private final TenantDAO tenantDAO = new TenantDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        super.initialize();
        filterComboBox.getItems().addAll("Tenants", "Rooms", "Payments");
        filterComboBox.setValue("Tenants");

        TableColumn<String[], String> colId      = (TableColumn<String[], String>) recordsTable.getColumns().get(0);
        TableColumn<String[], String> colName    = (TableColumn<String[], String>) recordsTable.getColumns().get(1);
        TableColumn<String[], String> colDetails = (TableColumn<String[], String>) recordsTable.getColumns().get(2);
        TableColumn<String[], String> colStatus  = (TableColumn<String[], String>) recordsTable.getColumns().get(3);

        colId.setCellValueFactory(d      -> new SimpleStringProperty(d.getValue()[0]));
        colName.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[1]));
        colDetails.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colStatus.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[3]));

        filterComboBox.setOnAction(e -> loadRecords(""));
        loadRecords("");
    }

    @FXML
    private void onSearchButtonClick() {
        loadRecords(searchField.getText().trim().toLowerCase());
    }

    private void loadRecords(String keyword) {
        // Multithreading: run DB queries in background to keep the UI responsive
        final String filter = filterComboBox.getValue();

        Task<List<String[]>> loadTask = new Task<List<String[]>>() {
            @Override
            protected List<String[]> call() throws Exception {
                return buildRecordList(filter, keyword);
            }
        };

        loadTask.setOnSucceeded(event -> {
            ObservableList<String[]> items = FXCollections.observableArrayList(loadTask.getValue());
            recordsTable.setItems(items);
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    // Extracted to a plain method so it can be called from the background Task
    private List<String[]> buildRecordList(String filter, String keyword) {
        List<String[]> data = new ArrayList<>();

        if (filter.equals("Tenants")) {
            for (Tenant t : tenantDAO.getAllTenants()) {
                String details = "Phone: " + t.getContactNumber() + "  |  Email: " + t.getEmail();
                String status;
                if (t.getRoomId() > 0) {
                    status = "Assigned";
                } else {
                    status = "Unassigned";
                }
                if (keyword.isEmpty() || t.getName().toLowerCase().contains(keyword)
                        || t.getEmail().toLowerCase().contains(keyword)) {
                    data.add(new String[]{String.valueOf(t.getId()), t.getName(), details, status});
                }
            }
        } else if (filter.equals("Rooms")) {
            for (Room r : roomDAO.getAllRooms()) {
                String details = "Capacity: " + r.getCapacity()
                    + "  |  Rate: \u20b1" + String.format("%.2f", r.getMonthlyRate());
                if (keyword.isEmpty() || ("room " + r.getRoomNumber()).contains(keyword)
                        || r.getStatus().toLowerCase().contains(keyword)) {
                    data.add(new String[]{
                        String.valueOf(r.getId()), "Room " + r.getRoomNumber(), details, r.getStatus()
                    });
                }
            }
        } else if (filter.equals("Payments")) {
            for (Tenant t : tenantDAO.getAllTenants()) {
                for (Payment p : paymentDAO.getByTenantId(t.getId())) {
                    String details = "Month: " + p.getMonthCovered()
                        + "  |  \u20b1" + String.format("%.2f", p.getAmount());
                    if (keyword.isEmpty() || t.getName().toLowerCase().contains(keyword)
                            || p.getMonthCovered().toLowerCase().contains(keyword)) {
                        data.add(new String[]{String.valueOf(p.getId()), t.getName(), details, p.getStatus()});
                    }
                }
            }
        }

        return data;
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("dashboard/DashboardView.fxml");
    }
}
