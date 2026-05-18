package com.example.dorm.auth;

import com.example.dorm.util.SceneManager;
import com.example.dorm.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("TENANT", "ADMIN", "LANDLORD");
        roleComboBox.setValue("TENANT");
        errorLabel.setText("");
    }

    @FXML
    private void onLoginButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        User user = userDAO.authenticate(username, password, role);
        if (user != null) {
            Session.setCurrentUser(user);
            SceneManager.switchTo("dashboard/DashboardView.fxml");
        } else {
            errorLabel.setText("Invalid credentials. Please try again.");
        }
    }
}
