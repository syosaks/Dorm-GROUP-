package com.example.dorm.reservation;

import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
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

import java.time.LocalDate;

public class ReserveRoomController {
    @FXML private TableView<Room> roomsTable;

    private final RoomDAO roomDAO = new RoomDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final TenantDAO tenantDAO = new TenantDAO();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        TableColumn<Room, String>  colNumber   = (TableColumn<Room, String>)  roomsTable.getColumns().get(0);
        TableColumn<Room, Integer> colCapacity = (TableColumn<Room, Integer>) roomsTable.getColumns().get(1);
        TableColumn<Room, String>  colStatus   = (TableColumn<Room, String>)  roomsTable.getColumns().get(2);
        TableColumn<Room, Double>  colRate     = (TableColumn<Room, Double>)  roomsTable.getColumns().get(3);

        colNumber.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getRoomNumber()));
        colCapacity.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getCapacity()).asObject());
        colStatus.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue().getStatus()));
        colRate.setCellValueFactory(d     -> new SimpleDoubleProperty(d.getValue().getMonthlyRate()).asObject());

        loadRooms();
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
