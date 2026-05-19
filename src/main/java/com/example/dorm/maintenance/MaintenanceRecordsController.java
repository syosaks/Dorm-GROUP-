package com.example.dorm.maintenance;

import com.example.dorm.shared.BaseController;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.auth.User;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays RESOLVED maintenance records only.
 * Accessible to all roles; tenants see only their own resolved requests.
 */
public class MaintenanceRecordsController extends BaseController {
    @FXML private TableView<String[]> recordsTable;
    @FXML private Label summaryLabel;

    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private final TenantDAO tenantDAO = new TenantDAO();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        super.initialize();
        TableColumn<String[], String> colTenant   = (TableColumn<String[], String>) recordsTable.getColumns().get(0);
        TableColumn<String[], String> colDesc     = (TableColumn<String[], String>) recordsTable.getColumns().get(1);
        TableColumn<String[], String> colDate     = (TableColumn<String[], String>) recordsTable.getColumns().get(2);
        TableColumn<String[], String> colPriority = (TableColumn<String[], String>) recordsTable.getColumns().get(3);
        TableColumn<String[], String> colStatus   = (TableColumn<String[], String>) recordsTable.getColumns().get(4);

        // data: [0]=tenantName, [1]=description, [2]=requestDate, [3]=status, [4]=id(hidden), [5]=priority
        colTenant.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[0]));
        colDesc.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue()[1]));
        colDate.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue()[2]));
        colPriority.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[5]));
        colStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[3]));

        loadRecords();
    }

    private void loadRecords() {
        User user = Session.getCurrentUser();
        if (user == null) {
            summaryLabel.setText("Session expired. Please log in again.");
            return;
        }
        List<String[]> data;

        if ("TENANT".equals(user.getRole())) {
            Tenant tenant = tenantDAO.getByUserId(user.getId());
            if (tenant != null) {
                data = maintenanceDAO.getResolvedByTenantId(tenant.getId());
            } else {
                data = new ArrayList<>();
            }
        } else {
            data = maintenanceDAO.getAllResolvedForDisplay();
        }

        recordsTable.setItems(FXCollections.observableArrayList(data));
        summaryLabel.setText("Total Resolved: " + data.size() + " record(s)");
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("maintenance/MaintenanceView.fxml");
    }
}
