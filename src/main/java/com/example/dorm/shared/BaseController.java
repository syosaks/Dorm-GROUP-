package com.example.dorm.shared;

import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.Set;

public abstract class BaseController {

    @FXML protected VBox navContainer;

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(this::filterNavForRole);
    }

    protected void filterNavForRole() {
        if (navContainer == null) return;
        if (Session.getCurrentUser() == null) return;

        String role = Session.getCurrentUser().getRole();
        Set<String> allowed;

        if ("TENANT".equals(role)) {
            allowed = Set.of(
                "My Room Status", "Reserve Room", "Pay Rent",
                "Maintenance", "Maintenance Records", "Visitor Log"
            );
        } else if ("ADMIN".equals(role)) {
            allowed = Set.of(
                "Pay Rent", "Assign Room", "Approve Reservation",
                "View Records", "View Reports", "Add New Resident",
                "Maintenance", "Maintenance Records", "Visitor Log",
                "Dorm Buildings", "Furniture Mgmt"
            );
        } else if ("LANDLORD".equals(role)) {
            allowed = Set.of(
                "View Records", "View Reports",
                "Maintenance", "Maintenance Records", "Visitor Log"
            );
        } else {
            allowed = Set.of();
        }

        for (javafx.scene.Node node : navContainer.getChildren()) {
            if (node instanceof Button btn) {
                if (!allowed.contains(btn.getText())) {
                    btn.setVisible(false);
                    btn.setManaged(false);
                }
            }
        }
    }

    @FXML
    public void onNavClick(ActionEvent event) {
        String label = ((Button) event.getSource()).getText();
        switch (label) {
            case "My Room Status"      -> SceneManager.switchTo("tenant/TenantStatusView.fxml");
            case "Reserve Room"        -> SceneManager.switchTo("reservation/ReserveRoomView.fxml");
            case "Pay Rent"            -> SceneManager.switchTo("payment/PayRentView.fxml");
            case "Assign Room"         -> SceneManager.switchTo("room/AssignRoomView.fxml");
            case "Approve Reservation" -> SceneManager.switchTo("reservation/ApproveReservationView.fxml");
            case "View Records"        -> SceneManager.switchTo("tenant/ViewRecordsView.fxml");
            case "View Reports"        -> SceneManager.switchTo("report/ViewReportsView.fxml");
            case "Add New Resident"    -> SceneManager.switchTo("tenant/AddTenantView.fxml");
            case "Maintenance"         -> SceneManager.switchTo("maintenance/MaintenanceView.fxml");
            case "Maintenance Records" -> SceneManager.switchTo("maintenance/MaintenanceRecordsView.fxml");
            case "Visitor Log"         -> SceneManager.switchTo("visitor/VisitorLogView.fxml");
            case "Dorm Buildings"      -> SceneManager.switchTo("building/DormBuildingView.fxml");
            case "Furniture Mgmt"      -> SceneManager.switchTo("furniture/FurnitureView.fxml");
        }
    }
}
