package com.example.dorm.building;

import com.example.dorm.room.Room;
import com.example.dorm.room.RoomDAO;
import com.example.dorm.shared.BaseController;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class DormBuildingController extends BaseController {
    @FXML private TextField buildingNameField;
    @FXML private TextField totalFloorsField;
    @FXML private TextField addressField;
    @FXML private Label statusLabel;
    @FXML private TableView<String[]> buildingsTable;
    @FXML private ComboBox<String> buildingFilterCombo;
    @FXML private TableView<String[]> roomsTable;

    private final DormBuildingDAO buildingDAO = new DormBuildingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private List<DormBuilding> buildingList;

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        super.initialize();
        // Buildings table
        TableColumn<String[], String> colName  = (TableColumn<String[], String>) buildingsTable.getColumns().get(0);
        TableColumn<String[], String> colFloor = (TableColumn<String[], String>) buildingsTable.getColumns().get(1);
        TableColumn<String[], String> colAddr  = (TableColumn<String[], String>) buildingsTable.getColumns().get(2);
        TableColumn<String[], String> colRooms = (TableColumn<String[], String>) buildingsTable.getColumns().get(3);

        colName.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[0]));
        colFloor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colAddr.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[2]));
        colRooms.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[3]));

        // Rooms table
        TableColumn<String[], String> rRoom  = (TableColumn<String[], String>) roomsTable.getColumns().get(0);
        TableColumn<String[], String> rBldg  = (TableColumn<String[], String>) roomsTable.getColumns().get(1);
        TableColumn<String[], String> rFloor = (TableColumn<String[], String>) roomsTable.getColumns().get(2);
        TableColumn<String[], String> rCap   = (TableColumn<String[], String>) roomsTable.getColumns().get(3);
        TableColumn<String[], String> rStat  = (TableColumn<String[], String>) roomsTable.getColumns().get(4);
        TableColumn<String[], String> rRate  = (TableColumn<String[], String>) roomsTable.getColumns().get(5);

        rRoom.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[0]));
        rBldg.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[1]));
        rFloor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        rCap.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[3]));
        rStat.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[4]));
        rRate.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[5]));

        loadData();

        buildingFilterCombo.setOnAction(e -> filterRooms());
    }

    private void loadData() {
        buildingsTable.setItems(FXCollections.observableArrayList(buildingDAO.getAllForDisplay()));

        buildingList = buildingDAO.getAllBuildings();
        buildingFilterCombo.getItems().clear();
        buildingFilterCombo.getItems().add("All Buildings");
        for (DormBuilding b : buildingList) buildingFilterCombo.getItems().add(b.getBuildingName());
        buildingFilterCombo.setValue("All Buildings");

        roomsTable.setItems(FXCollections.observableArrayList(roomDAO.getAllWithBuilding()));
    }

    private void filterRooms() {
        String selected = buildingFilterCombo.getValue();
        if (selected == null || "All Buildings".equals(selected)) {
            roomsTable.setItems(FXCollections.observableArrayList(roomDAO.getAllWithBuilding()));
        } else {
            // Filter rooms by the selected building name (column index 1)
            List<String[]> filtered = new ArrayList<>();
            for (String[] room : roomDAO.getAllWithBuilding()) {
                if (selected.equals(room[1])) {
                    filtered.add(room);
                }
            }
            roomsTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    @FXML
    private void onAddBuildingClick() {
        String name    = buildingNameField.getText().trim();
        String floorsStr = totalFloorsField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || floorsStr.isEmpty() || address.isEmpty()) {
            setStatus("Building name, floors, and address are all required.", false);
            return;
        }

        int floors;
        try {
            floors = Integer.parseInt(floorsStr);
            if (floors < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            setStatus("Total floors must be a positive integer.", false);
            return;
        }

        int id = buildingDAO.add(name, floors, address);
        if (id == -1) {
            setStatus("Failed to add building. Name may already exist.", false);
            return;
        }

        setStatus("Building \"" + name + "\" added successfully!", true);
        buildingNameField.clear();
        totalFloorsField.clear();
        addressField.clear();
        loadData();
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
