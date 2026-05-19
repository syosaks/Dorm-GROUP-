package com.example.dorm.room;

import com.example.dorm.shared.BaseController;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.DatabaseConnection;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AssignRoomController extends BaseController {
    @FXML private ComboBox<String> tenantComboBox;
    @FXML private ComboBox<String> roomComboBox;
    @FXML private TableView<String[]> assignmentsTable;

    private final TenantDAO tenantDAO = new TenantDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private List<Tenant> unassignedTenants;
    private List<Room> availableRooms;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        super.initialize();
        TableColumn<String[], String> colTenant   = (TableColumn<String[], String>) assignmentsTable.getColumns().get(0);
        TableColumn<String[], String> colRoom     = (TableColumn<String[], String>) assignmentsTable.getColumns().get(1);
        TableColumn<String[], String> colCapacity = (TableColumn<String[], String>) assignmentsTable.getColumns().get(2);
        TableColumn<String[], String> colRate     = (TableColumn<String[], String>) assignmentsTable.getColumns().get(3);

        colTenant.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[0]));
        colRoom.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue()[1]));
        colCapacity.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colRate.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue()[3]));

        loadData();
    }

    private void loadData() {
        unassignedTenants = tenantDAO.getUnassignedTenants();
        tenantComboBox.getItems().clear();
        for (Tenant t : unassignedTenants) tenantComboBox.getItems().add(t.getName());

        availableRooms = roomDAO.getAvailableRooms();
        roomComboBox.getItems().clear();
        for (Room r : availableRooms) {
            roomComboBox.getItems().add("Room " + r.getRoomNumber()
                + "  (Cap: " + r.getCapacity()
                + "  |  \u20b1" + String.format("%.2f", r.getMonthlyRate()) + ")");
        }
        assignmentsTable.setItems(FXCollections.observableArrayList(tenantDAO.getAssignmentsDisplay()));
    }

    @FXML
    private void onAssignButtonClick() {
        int tIdx = tenantComboBox.getSelectionModel().getSelectedIndex();
        int rIdx = roomComboBox.getSelectionModel().getSelectedIndex();
        if (tIdx < 0 || rIdx < 0) { showAlert("Please select both a tenant and a room."); return; }

        Tenant tenant = unassignedTenants.get(tIdx);
        Room room = availableRooms.get(rIdx);

        // ── Atomic 2-step transaction ──────────────────────────────────────────
        DatabaseConnection db = DatabaseConnection.getInstance();
        try {
            db.beginTransaction();
            tenantDAO.updateRoomId(tenant.getId(), room.getId());
            roomDAO.updateStatus(room.getId(), "OCCUPIED");
            db.commit();
            showAlert(tenant.getName() + " assigned to Room " + room.getRoomNumber() + ".");
            loadData();
        } catch (Exception e) {
            db.rollback();
            showAlert("Assignment failed. Changes rolled back. Try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onVacateButtonClick() {
        String[] selected = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Select an assignment to vacate."); return; }

        String tenantName = selected[0];
        String roomNumber = selected[1];
        int tenantId = Integer.parseInt(selected[4]);
        int roomId   = Integer.parseInt(selected[5]);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Confirm Vacate");
        confirm.setContentText("Remove " + tenantName + " from Room " + roomNumber + "?");

        // Show dialog and check if user clicked OK
        java.util.Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseConnection db = DatabaseConnection.getInstance();
            try {
                db.beginTransaction();
                tenantDAO.vacateRoom(tenantId);
                roomDAO.updateStatus(roomId, "AVAILABLE");
                db.commit();
                showAlert(tenantName + " removed from Room " + roomNumber + ". Room is now AVAILABLE.");
                loadData();
            } catch (Exception e) {
                db.rollback();
                showAlert("Vacate failed. Changes rolled back. Try again.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onBackButtonClick() { SceneManager.switchTo("dashboard/DashboardView.fxml"); }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
