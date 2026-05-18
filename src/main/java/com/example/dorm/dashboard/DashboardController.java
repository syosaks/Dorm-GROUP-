package com.example.dorm.dashboard;

import com.example.dorm.auth.User;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML private Label welcomeLabel;

    // Tenant self-service
    @FXML private Button btnMyStatus;

    // Existing buttons
    @FXML private Button btnReserveRoom;
    @FXML private Button btnPayRent;
    @FXML private Button btnAssignRoom;
    @FXML private Button btnApproveReservation;
    @FXML private Button btnViewRecords;
    @FXML private Button btnViewReports;
    @FXML private Button btnMaintenance;
    @FXML private Button btnAddTenant;

    // New DormLink buttons
    @FXML private Button btnDormBuildings;
    @FXML private Button btnVisitorLog;
    @FXML private Button btnFurniture;
    @FXML private Button btnMaintenanceRecords;

    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        if (user == null) return;

        welcomeLabel.setText("Welcome, " + user.getUsername() + "  |  Role: " + user.getRole());

        // Hide all, then reveal by role
        hide(btnMyStatus);
        hide(btnReserveRoom); hide(btnPayRent); hide(btnAssignRoom);
        hide(btnApproveReservation); hide(btnViewRecords); hide(btnViewReports);
        hide(btnMaintenance); hide(btnAddTenant);
        hide(btnDormBuildings); hide(btnVisitorLog); hide(btnFurniture);
        hide(btnMaintenanceRecords);

        String role = user.getRole();

        if (role.equals("TENANT")) {
            show(btnMyStatus);
            show(btnReserveRoom);
            show(btnPayRent);
            show(btnMaintenance);
            show(btnMaintenanceRecords);
            show(btnVisitorLog);
        } else if (role.equals("ADMIN")) {
            show(btnPayRent);
            btnPayRent.setText("Record Payment");
            show(btnAssignRoom);
            show(btnApproveReservation);
            show(btnViewRecords);
            show(btnViewReports);
            show(btnMaintenance);
            show(btnMaintenanceRecords);
            show(btnAddTenant);
            show(btnDormBuildings);
            show(btnVisitorLog);
            show(btnFurniture);
        } else if (role.equals("LANDLORD")) {
            show(btnViewRecords);
            show(btnViewReports);
            show(btnMaintenance);
            show(btnMaintenanceRecords);
            show(btnVisitorLog);
        }
    }

    private void show(Button btn) { btn.setVisible(true);  btn.setManaged(true); }
    private void hide(Button btn) { btn.setVisible(false); btn.setManaged(false); }

    @FXML private void onMyStatus()             { SceneManager.switchTo("tenant/TenantStatusView.fxml"); }
    @FXML private void onReserveRoom()          { SceneManager.switchTo("reservation/ReserveRoomView.fxml"); }
    @FXML private void onPayRent()              { SceneManager.switchTo("payment/PayRentView.fxml"); }
    @FXML private void onAssignRoom()           { SceneManager.switchTo("room/AssignRoomView.fxml"); }
    @FXML private void onApproveReservation()   { SceneManager.switchTo("reservation/ApproveReservationView.fxml"); }
    @FXML private void onViewRecords()          { SceneManager.switchTo("tenant/ViewRecordsView.fxml"); }
    @FXML private void onViewReports()          { SceneManager.switchTo("report/ViewReportsView.fxml"); }
    @FXML private void onMaintenance()          { SceneManager.switchTo("maintenance/MaintenanceView.fxml"); }
    @FXML private void onAddTenant()            { SceneManager.switchTo("tenant/AddTenantView.fxml"); }
    @FXML private void onDormBuildings()        { SceneManager.switchTo("building/DormBuildingView.fxml"); }
    @FXML private void onVisitorLog()           { SceneManager.switchTo("visitor/VisitorLogView.fxml"); }
    @FXML private void onFurniture()            { SceneManager.switchTo("furniture/FurnitureView.fxml"); }
    @FXML private void onMaintenanceRecords()   { SceneManager.switchTo("maintenance/MaintenanceRecordsView.fxml"); }

    @FXML
    private void onLogout() {
        Session.clear();
        SceneManager.switchTo("auth/LoginView.fxml");
    }
}
