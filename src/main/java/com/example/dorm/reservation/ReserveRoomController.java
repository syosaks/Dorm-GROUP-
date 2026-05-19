package com.example.dorm.reservation;

import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.shared.BaseController;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.auth.User;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;

public class ReserveRoomController extends BaseController {
    @FXML private TableView<Room> roomsTable;
    @FXML private Label statusLabel;
    @FXML private Label statusDot;
    @FXML private HBox statusBanner;

    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final TenantDAO tenantDAO = new TenantDAO();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        super.initialize();
        TableColumn<Room, String>  colNumber   = (TableColumn<Room, String>)  roomsTable.getColumns().get(0);
        TableColumn<Room, Integer> colCapacity = (TableColumn<Room, Integer>) roomsTable.getColumns().get(1);
        TableColumn<Room, String>  colStatus   = (TableColumn<Room, String>)  roomsTable.getColumns().get(2);
        TableColumn<Room, Double>  colRate     = (TableColumn<Room, Double>)  roomsTable.getColumns().get(3);

        colNumber.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getRoomNumber()));
        colCapacity.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCapacity()).asObject());
        colStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getStatus()));
        colRate.setCellValueFactory(d     -> new SimpleDoubleProperty(d.getValue().getMonthlyRate()).asObject());

        loadRooms();
        loadStatusBanner();
    }

    private void loadStatusBanner() {
        User user = Session.getCurrentUser();
        if (user == null || statusLabel == null) return;
        Tenant tenant = tenantDAO.getByUserId(user.getId());
        if (tenant == null) return;

        // Priority 1: already assigned to a room
        if (tenant.getRoomId() > 0) {
            statusBanner.setStyle(statusBanner.getStyle().replace("#1e2035", "#1a2e1a") + "; -fx-border-color: #2e7d32;");
            statusDot.setStyle(statusDot.getStyle().replace("#555570", "#4caf50"));
            statusLabel.setStyle("-fx-text-fill: #66bb6a; -fx-font-size: 13px; -fx-font-weight: bold;");
            statusLabel.setText("You are currently assigned to a room. No new reservation needed.");
            return;
        }

        // Priority 2: pending or approved reservation
        String[] res = reservationDAO.getLatestReservationForTenant(tenant.getId());
        if (res != null) {
            String roomNum = res[0];
            String status  = res[1];
            String date    = res[2];
            switch (status) {
                case "PENDING" -> {
                    statusBanner.setStyle(statusBanner.getStyle().replace("#1e2035", "#2e2a10") + "; -fx-border-color: #f9a825;");
                    statusDot.setStyle(statusDot.getStyle().replace("#555570", "#f5c400"));
                    statusLabel.setStyle("-fx-text-fill: #f5c400; -fx-font-size: 13px; -fx-font-weight: bold;");
                    statusLabel.setText("Pending reservation for Room " + roomNum + " — submitted " + date + ". Waiting for admin approval.");
                }
                case "APPROVED" -> {
                    statusBanner.setStyle(statusBanner.getStyle().replace("#1e2035", "#0d2137") + "; -fx-border-color: #1565c0;");
                    statusDot.setStyle(statusDot.getStyle().replace("#555570", "#42a5f5"));
                    statusLabel.setStyle("-fx-text-fill: #42a5f5; -fx-font-size: 13px; -fx-font-weight: bold;");
                    statusLabel.setText("Reservation for Room " + roomNum + " was APPROVED on " + date + ".");
                }
            }
            return;
        }

        // No reservation at all
        statusDot.setStyle(statusDot.getStyle().replace("#555570", "#555570"));
        statusLabel.setStyle("-fx-text-fill: #8a8a9a; -fx-font-size: 13px;");
        statusLabel.setText("No active reservation — select a room below and click Reserve.");
    }

    private void loadRooms() {
        roomsTable.setItems(FXCollections.observableArrayList(roomDAO.getAvailableRooms()));
    }

    @FXML
    private void onReserveButtonClick() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a room from the list.");
            return;
        }

        User user = Session.getCurrentUser();
        if (user == null) {
            showAlert("Session expired. Please log in again.");
            return;
        }
        Tenant tenant = tenantDAO.getByUserId(user.getId());
        if (tenant == null) {
            showAlert("No tenant profile found for your account. Contact the admin.");
            return;
        }

        // Guard: tenant already has a room assigned
        if (tenant.getRoomId() > 0) {
            showAlert("You are already assigned to a room.\n"
                + "Contact admin if you need to request a room change.");
            return;
        }

        // Guard: tenant already has a pending reservation
        if (reservationDAO.hasPendingReservation(tenant.getId())) {
            showAlert("You already have a pending reservation.\n"
                + "Please wait for admin approval before submitting another.");
            return;
        }

        Reservation r = new Reservation();
        r.setTenantId(tenant.getId());
        r.setRoomId(selected.getId());
        r.setRequestDate(LocalDate.now().toString());
        r.setStatus("PENDING");
        reservationDAO.add(r);

        showAlert("Reservation submitted for Room " + selected.getRoomNumber() + ".\n"
            + "Waiting for admin approval.");
        loadRooms();
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
