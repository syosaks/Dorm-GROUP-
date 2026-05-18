package com.example.dorm.tenant;

import com.example.dorm.auth.UserDAO;
import com.example.dorm.util.DatabaseConnection;
import com.example.dorm.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AddTenantController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField contactField;
    @FXML private TextField emailField;
    @FXML private Label statusLabel;
    @FXML private TableView<String[]> tenantsTable;

    private final UserDAO userDAO = new UserDAO();
    private final TenantDAO tenantDAO = new TenantDAO();

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        TableColumn<String[], String> colName    = (TableColumn<String[], String>) tenantsTable.getColumns().get(0);
        TableColumn<String[], String> colUser    = (TableColumn<String[], String>) tenantsTable.getColumns().get(1);
        TableColumn<String[], String> colContact = (TableColumn<String[], String>) tenantsTable.getColumns().get(2);
        TableColumn<String[], String> colEmail   = (TableColumn<String[], String>) tenantsTable.getColumns().get(3);
        TableColumn<String[], String> colRoom    = (TableColumn<String[], String>) tenantsTable.getColumns().get(4);

        // data: [0]=name, [1]=username, [2]=contact, [3]=email, [4]=room
        colName.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[0]));
        colUser.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[1]));
        colContact.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[2]));
        colEmail.setCellValueFactory(d   -> new SimpleStringProperty(d.getValue()[3]));
        colRoom.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue()[4]));

        loadTenants();
    }

    private void loadTenants() {
        // Multithreading: run the database query on a background thread
        // so the UI does not freeze while loading tenant data.
        Task<List<String[]>> loadTask = new Task<List<String[]>>() {
            @Override
            protected List<String[]> call() throws Exception {
                return tenantDAO.getTenantsWithDetails();
            }
        };

        // When the background task finishes, update the table on the UI thread
        loadTask.setOnSucceeded(event -> {
            tenantsTable.setItems(FXCollections.observableArrayList(loadTask.getValue()));
        });

        // Start the background thread (daemon = stops when app closes)
        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onAddButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String name     = nameField.getText().trim();
        String contact  = contactField.getText().trim();
        String email    = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            setStatus("Username, password, and full name are required.", false);
            return;
        }

        if (password.length() < 4) {
            setStatus("Password must be at least 4 characters.", false);
            return;
        }

        if (userDAO.usernameExists(username)) {
            setStatus("Username '" + username + "' is already taken. Choose another.", false);
            return;
        }

        DatabaseConnection db = DatabaseConnection.getInstance();
        try {
            db.beginTransaction();
            int userId = userDAO.addUser(username, password, "TENANT");
            if (userId == -1) {
                db.rollback();
                setStatus("Failed to create user account. Try again.", false);
                return;
            }
            tenantDAO.addTenant(name, contact, email, userId);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            setStatus("Failed to add tenant. Try again.", false);
            return;
        }
        setStatus("Tenant '" + name + "' added successfully! Login: " + username, true);

        usernameField.clear();
        passwordField.clear();
        nameField.clear();
        contactField.clear();
        emailField.clear();
        loadTenants();
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
