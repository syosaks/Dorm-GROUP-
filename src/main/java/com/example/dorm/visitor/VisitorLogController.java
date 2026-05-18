package com.example.dorm.visitor;

import com.example.dorm.tenant.Tenant;
import com.example.dorm.tenant.TenantDAO;
import com.example.dorm.auth.User;
import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class VisitorLogController {
    @FXML private VBox submitSection;
    @FXML private TextField visitorNameField;
    @FXML private TextField purposeField;
    @FXML private TextField timeInField;
    @FXML private TextField timeOutField;
    @FXML private TableView<String[]> logsTable;
    @FXML private Button btnUpdateTimeOut;

    private final VisitorLogDAO logDAO = new VisitorLogDAO();
    private final TenantDAO tenantDAO = new TenantDAO();

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        TableColumn<String[], String> colVisitor   = (TableColumn<String[], String>) logsTable.getColumns().get(0);
        TableColumn<String[], String> colResident  = (TableColumn<String[], String>) logsTable.getColumns().get(1);
        TableColumn<String[], String> colDate      = (TableColumn<String[], String>) logsTable.getColumns().get(2);
        TableColumn<String[], String> colTimeIn    = (TableColumn<String[], String>) logsTable.getColumns().get(3);
        TableColumn<String[], String> colTimeOut   = (TableColumn<String[], String>) logsTable.getColumns().get(4);
        TableColumn<String[], String> colPurpose   = (TableColumn<String[], String>) logsTable.getColumns().get(5);

        // data: [0]=visitorName, [1]=residentName, [2]=visitDate, [3]=timeIn, [4]=timeOut, [5]=purpose, [6]=id
        colVisitor.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[0]));
        colResident.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        colDate.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue()[2]));
        colTimeIn.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[3]));
        colTimeOut.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[4]));
        colPurpose.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue()[5]));

        User user = Session.getCurrentUser();
        boolean isTenant = "TENANT".equals(user.getRole());

        submitSection.setVisible(isTenant);
        submitSection.setManaged(isTenant);
        btnUpdateTimeOut.setVisible(isTenant);
        btnUpdateTimeOut.setManaged(isTenant);

        loadLogs();
    }

    private void loadLogs() {
        User user = Session.getCurrentUser();
        List<String[]> data;
        if ("TENANT".equals(user.getRole())) {
            Tenant tenant = tenantDAO.getByUserId(user.getId());
            if (tenant != null) {
                data = logDAO.getByTenantId(tenant.getId());
            } else {
                data = new ArrayList<>();
            }
        } else {
            data = logDAO.getAllForDisplay();
        }
        logsTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    private void onLogVisitorClick() {
        String visitorName = visitorNameField.getText().trim();
        String purpose     = purposeField.getText().trim();
        String timeIn      = timeInField.getText().trim();
        String timeOut     = timeOutField.getText().trim();

        if (visitorName.isEmpty()) { showAlert("Visitor name is required."); return; }
        if (purpose.isEmpty())     { showAlert("Purpose of visit is required."); return; }
        if (timeIn.isEmpty())      { showAlert("Time In is required (format HH:mm)."); return; }

        if (!isValidTime(timeIn))  { showAlert("Time In must be in HH:mm format (e.g. 14:30)."); return; }

        if (!timeOut.isEmpty()) {
            if (!isValidTime(timeOut)) { showAlert("Time Out must be in HH:mm format (e.g. 16:00)."); return; }
            if (!LocalTime.parse(timeOut, TIME_FMT).isAfter(LocalTime.parse(timeIn, TIME_FMT))) {
                showAlert("Time Out must be after Time In."); return;
            }
        }

        User user = Session.getCurrentUser();
        Tenant tenant = tenantDAO.getByUserId(user.getId());
        if (tenant == null) { showAlert("No resident profile found. Contact admin."); return; }

        VisitorLog v = new VisitorLog();
        v.setVisitorName(visitorName);
        v.setTenantId(tenant.getId());
        v.setVisitDate(LocalDate.now().toString());
        v.setTimeIn(timeIn);
        v.setTimeOut(timeOut);
        v.setPurpose(purpose);
        logDAO.add(v);

        showAlert("Visitor \"" + visitorName + "\" logged successfully.");
        visitorNameField.clear();
        purposeField.clear();
        timeInField.clear();
        timeOutField.clear();
        loadLogs();
    }

    @FXML
    private void onUpdateTimeOutClick() {
        String[] selected = logsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Please select a log entry to update."); return; }

        TextInputDialog dialog = new TextInputDialog(selected[4].equals("—") ? "" : selected[4]);
        dialog.setHeaderText("Update Time Out");
        dialog.setContentText("Enter Time Out (HH:mm):");
        // Show dialog and get the entered time
        java.util.Optional<String> dialogResult = dialog.showAndWait();
        if (dialogResult.isPresent()) {
            String timeOut = dialogResult.get();
            if (timeOut.isEmpty()) {
                showAlert("Time Out cannot be empty.");
            } else if (!isValidTime(timeOut)) {
                showAlert("Time Out must be in HH:mm format.");
            } else if (!LocalTime.parse(timeOut, TIME_FMT).isAfter(LocalTime.parse(selected[3], TIME_FMT))) {
                showAlert("Time Out must be after Time In (" + selected[3] + ").");
            } else {
                int id = Integer.parseInt(selected[6]);
                logDAO.updateTimeOut(id, timeOut);
                showAlert("Time Out updated.");
                loadLogs();
            }
        }
    }

    private boolean isValidTime(String t) {
        try { LocalTime.parse(t, TIME_FMT); return true; }
        catch (DateTimeParseException e) { return false; }
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
