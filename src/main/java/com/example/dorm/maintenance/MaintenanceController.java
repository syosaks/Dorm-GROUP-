package com.example.dorm.maintenance;

import com.example.dorm.shared.BaseController;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.auth.User;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceController extends BaseController {
    @FXML private VBox submitSection;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TableView<String[]> requestsTable;
    @FXML private Button btnInProgress;
    @FXML private Button btnResolved;

    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private final TenantDAO tenantDAO = new TenantDAO();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        super.initialize();
        TableColumn<String[], String> colTenant   = (TableColumn<String[], String>) requestsTable.getColumns().get(0);
        TableColumn<String[], String> colDesc     = (TableColumn<String[], String>) requestsTable.getColumns().get(1);
        TableColumn<String[], String> colDate     = (TableColumn<String[], String>) requestsTable.getColumns().get(2);
        TableColumn<String[], String> colStatus   = (TableColumn<String[], String>) requestsTable.getColumns().get(3);
        TableColumn<String[], String> colPriority = (TableColumn<String[], String>) requestsTable.getColumns().get(4);

        // data: [0]=tenantName, [1]=description, [2]=requestDate, [3]=status, [4]=id(hidden), [5]=priority
        colTenant.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[0]));
        colDesc.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue()[1]));
        colDate.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue()[2]));
        colStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[3]));
        colPriority.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[5]));

        User user = Session.getCurrentUser();
        if (user == null) return;
        boolean isTenant = "TENANT".equals(user.getRole());

        submitSection.setVisible(isTenant);
        submitSection.setManaged(isTenant);
        btnInProgress.setVisible(!isTenant);
        btnInProgress.setManaged(!isTenant);
        btnResolved.setVisible(!isTenant);
        btnResolved.setManaged(!isTenant);

        if (isTenant) {
            priorityCombo.getItems().addAll("LOW", "MEDIUM", "HIGH");
            priorityCombo.setValue("MEDIUM");
        }

        loadRequests();
    }

    private void loadRequests() {
        User user = Session.getCurrentUser();
        if (user == null) return;

        // Multithreading: load maintenance data in background so UI stays responsive
        Task<List<String[]>> loadTask = new Task<List<String[]>>() {
            @Override
            protected List<String[]> call() throws Exception {
                if ("TENANT".equals(user.getRole())) {
                    Tenant tenant = tenantDAO.getByUserId(user.getId());
                    if (tenant != null) {
                        return maintenanceDAO.getActiveByTenantId(tenant.getId());
                    } else {
                        return new ArrayList<>();
                    }
                } else {
                    return maintenanceDAO.getAllActiveForDisplay();
                }
            }
        };

        loadTask.setOnSucceeded(event -> {
            requestsTable.setItems(FXCollections.observableArrayList(loadTask.getValue()));
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onSubmitButtonClick() {
        String desc = descriptionArea.getText().trim();
        if (desc.isEmpty()) { showAlert("Please describe the issue before submitting."); return; }

        User user = Session.getCurrentUser();
        if (user == null) { showAlert("Session expired. Please log in again."); return; }
        Tenant tenant = tenantDAO.getByUserId(user.getId());
        if (tenant == null) { showAlert("No resident profile found. Contact the admin."); return; }

        String priority = priorityCombo.getValue() != null ? priorityCombo.getValue() : "MEDIUM";

        MaintenanceRequest req = new MaintenanceRequest();
        req.setTenantId(tenant.getId());
        req.setDescription(desc);
        req.setRequestDate(LocalDate.now().toString());
        req.setStatus("PENDING");
        req.setPriority(priority);
        maintenanceDAO.add(req);

        showAlert("Maintenance request submitted (" + priority + " priority).\nWe will attend to it shortly.");
        descriptionArea.clear();
        loadRequests();
    }

    @FXML
    private void onMarkInProgress() { updateSelectedStatus("IN_PROGRESS", "Marked as In Progress."); }

    @FXML
    private void onMarkResolved() { updateSelectedStatus("RESOLVED", "Marked as Resolved."); }

    private void updateSelectedStatus(String newStatus, String msg) {
        String[] selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Please select a request from the table."); return; }
        if (newStatus.equals(selected[3])) { showAlert("Request is already " + newStatus + "."); return; }
        int id = Integer.parseInt(selected[4]);
        maintenanceDAO.updateStatus(id, newStatus);
        showAlert(msg);
        loadRequests();
    }

    @FXML
    private void onViewRecordsClick() { SceneManager.switchTo("maintenance/MaintenanceRecordsView.fxml"); }

    @FXML
    private void onBackButtonClick() { SceneManager.switchTo("dashboard/DashboardView.fxml"); }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
