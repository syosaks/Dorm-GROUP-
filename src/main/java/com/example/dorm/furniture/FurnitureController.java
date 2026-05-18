package com.example.dorm.furniture;

import com.example.dorm.maintenance.MaintenanceDAO;
import com.example.dorm.maintenance.MaintenanceRequest;
import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class FurnitureController {
    @FXML private ComboBox<String> roomFilterCombo;
    @FXML private TableView<String[]> furnitureTable;
    @FXML private ComboBox<String> addRoomCombo;
    @FXML private TextField itemTypeField;
    @FXML private ComboBox<String> conditionCombo;
    @FXML private TextField serialField;
    @FXML private Label statusLabel;

    private final FurnitureDAO furnitureDAO = new FurnitureDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final TenantDAO tenantDAO = new TenantDAO();
    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private List<Room> allRooms;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        TableColumn<String[], String> colRoom   = (TableColumn<String[], String>) furnitureTable.getColumns().get(0);
        TableColumn<String[], String> colItem   = (TableColumn<String[], String>) furnitureTable.getColumns().get(1);
        TableColumn<String[], String> colCond   = (TableColumn<String[], String>) furnitureTable.getColumns().get(2);
        TableColumn<String[], String> colSerial = (TableColumn<String[], String>) furnitureTable.getColumns().get(3);

        // data: [0]=roomNumber, [1]=itemType, [2]=condition, [3]=serialNumber, [4]=id(hidden), [5]=roomId(hidden)
        colRoom.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[0]));
        colItem.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[1]));
        colCond.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[2]));
        colSerial.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));

        conditionCombo.getItems().addAll("GOOD", "DAMAGED", "BROKEN");
        conditionCombo.setValue("GOOD");

        allRooms = roomDAO.getAllRooms();
        roomFilterCombo.getItems().add("All Rooms");
        for (Room r : allRooms) {
            String label = "Room " + r.getRoomNumber();
            roomFilterCombo.getItems().add(label);
            addRoomCombo.getItems().add(label);
        }
        roomFilterCombo.setValue("All Rooms");

        roomFilterCombo.setOnAction(e -> loadFurniture());
        loadFurniture();
    }

    private void loadFurniture() {
        String selected = roomFilterCombo.getValue();
        List<String[]> data;
        if (selected == null || "All Rooms".equals(selected)) {
            data = furnitureDAO.getAllForDisplay();
        } else {
            int idx = roomFilterCombo.getItems().indexOf(selected) - 1; // -1 for "All Rooms"
            if (idx >= 0 && idx < allRooms.size()) {
                data = furnitureDAO.getByRoomId(allRooms.get(idx).getId());
            } else {
                data = furnitureDAO.getAllForDisplay();
            }
        }
        furnitureTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void onAddFurnitureClick() {
        int rIdx = addRoomCombo.getSelectionModel().getSelectedIndex();
        String itemType = itemTypeField.getText().trim();
        String cond     = conditionCombo.getValue();
        String serial   = serialField.getText().trim();

        if (rIdx < 0) { setStatus("Please select a room.", false); return; }
        if (itemType.isEmpty()) { setStatus("Item type is required.", false); return; }
        if (serial.isEmpty())   { setStatus("Serial number is required.", false); return; }

        Room room = allRooms.get(rIdx);
        furnitureDAO.add(room.getId(), itemType, cond, serial);

        // Business rule: BROKEN condition at creation auto-generates a HIGH priority ticket
        if ("BROKEN".equals(cond)) {
            autoCreateMaintenanceTicket(room, itemType, serial);
        }

        setStatus("Furniture \"" + itemType + "\" added to " + "Room " + room.getRoomNumber() + ".", true);
        itemTypeField.clear();
        serialField.clear();
        conditionCombo.setValue("GOOD");
        loadFurniture();
    }

    @FXML
    private void onMarkGoodClick()    { updateSelectedCondition("GOOD"); }

    @FXML
    private void onMarkDamagedClick() { updateSelectedCondition("DAMAGED"); }

    @FXML
    private void onMarkBrokenClick()  { updateSelectedCondition("BROKEN"); }

    private void updateSelectedCondition(String newCond) {
        String[] selected = furnitureTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setStatus("Select a furniture item first.", false); return; }
        if (newCond.equals(selected[2])) { setStatus("Item is already " + newCond + ".", false); return; }

        int furnitureId = Integer.parseInt(selected[4]);
        int roomId      = Integer.parseInt(selected[5]);
        furnitureDAO.updateCondition(furnitureId, newCond);

        // Business rule: changing to BROKEN auto-creates a HIGH priority maintenance ticket
        if ("BROKEN".equals(newCond)) {
            Room room = roomDAO.getRoomById(roomId);
            if (room != null) autoCreateMaintenanceTicket(room, selected[1], selected[3]);
        }

        setStatus("Condition updated to " + newCond + ".", true);
        loadFurniture();
    }

    private void autoCreateMaintenanceTicket(Room room, String itemType, String serial) {
        // Assign ticket to the current tenant of the room (if any)
        Tenant tenant = findTenantByRoom(room.getId());
        if (tenant == null) {
            setStatus("Furniture marked BROKEN — no active resident in room, ticket not created.",false);
            return;
        }
        MaintenanceRequest ticket = new MaintenanceRequest();
        ticket.setTenantId(tenant.getId());
        ticket.setDescription("[AUTO] " + itemType + " (S/N: " + serial + ") reported BROKEN in Room "
            + room.getRoomNumber() + ". Immediate repair required.");
        ticket.setRequestDate(LocalDate.now().toString());
        ticket.setStatus("PENDING");
        ticket.setPriority("HIGH");
        maintenanceDAO.add(ticket);
        setStatus("Furniture marked BROKEN — HIGH priority maintenance ticket auto-created!", true);
    }

    private Tenant findTenantByRoom(int roomId) {
        for (Tenant t : tenantDAO.getAllTenants()) {
            if (t.getRoomId() == roomId) return t;
        }
        return null;
    }

    private void setStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setStyle(success
            ? "-fx-text-fill: #2E7D32; -fx-font-weight: bold;"
            : "-fx-text-fill: #C62828; -fx-font-weight: bold;");
    }

    @FXML
    private void onBackButtonClick() {
        SceneManager.switchTo("dashboard/DashboardView.fxml");
    }
}
