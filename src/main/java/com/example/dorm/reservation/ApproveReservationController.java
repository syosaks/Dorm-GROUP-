package com.example.dorm.reservation;

import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.DatabaseConnection;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ApproveReservationController {
    @FXML private TableView<String[]> reservationsTable;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final TenantDAO tenantDAO = new TenantDAO();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        TableColumn<String[], String> colTenant = (TableColumn<String[], String>) reservationsTable.getColumns().get(0);
        TableColumn<String[], String> colRoom   = (TableColumn<String[], String>) reservationsTable.getColumns().get(1);
        TableColumn<String[], String> colDate   = (TableColumn<String[], String>) reservationsTable.getColumns().get(2);
        TableColumn<String[], String> colStatus = (TableColumn<String[], String>) reservationsTable.getColumns().get(3);

        // data[0]=id, data[1]=tenantName, data[2]=roomNumber, data[3]=requestDate,
        // data[4]=status, data[5]=tenantId (hidden), data[6]=roomId (hidden)
        colTenant.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colRoom.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[2]));
        colDate.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[3]));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[4]));

        loadReservations();
    }

    private void loadReservations() {
        List<String[]> data = reservationDAO.getReservationsForDisplay();
        reservationsTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void onApproveButtonClick() {
        String[] selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Please select a reservation."); return; }
        if ("APPROVED".equals(selected[4])) { showAlert("This reservation is already approved."); return; }
        if ("REJECTED".equals(selected[4])) { showAlert("Cannot approve a rejected reservation."); return; }

        int reservationId = Integer.parseInt(selected[0]);
        int tenantId      = Integer.parseInt(selected[5]);
        int roomId        = Integer.parseInt(selected[6]);

        // Verify room is still available before committing
        Room room = roomDAO.getRoomById(roomId);
        if (room == null || !"AVAILABLE".equals(room.getStatus())) {
            showAlert("Room " + selected[2] + " is no longer available. Cannot approve.");
            loadReservations();
            return;
        }

        // ── Atomic 3-step transaction ──────────────────────────────────────────
        DatabaseConnection db = DatabaseConnection.getInstance();
        try {
            db.beginTransaction();
            reservationDAO.updateStatus(reservationId, "APPROVED"); // 1. mark approved
            tenantDAO.updateRoomId(tenantId, roomId);               // 2. assign tenant
            roomDAO.updateStatus(roomId, "OCCUPIED");               // 3. mark room occupied
            db.commit();
            showAlert("Reservation approved.\n"
                + selected[1] + " has been assigned to Room " + selected[2] + ".");
            loadReservations();
        } catch (Exception e) {
            db.rollback();
            showAlert("Approval failed. All changes were rolled back. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onRejectButtonClick() {
        String[] selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Please select a reservation."); return; }
        if ("REJECTED".equals(selected[4])) { showAlert("This reservation is already rejected."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Confirm Rejection");
        confirm.setContentText("Reject reservation for " + selected[1]
            + " (Room " + selected[2] + ")?");

        // Show dialog and check if user clicked OK
        java.util.Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            reservationDAO.updateStatus(Integer.parseInt(selected[0]), "REJECTED");
            showAlert("Reservation rejected for " + selected[1] + ".");
            loadReservations();
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
