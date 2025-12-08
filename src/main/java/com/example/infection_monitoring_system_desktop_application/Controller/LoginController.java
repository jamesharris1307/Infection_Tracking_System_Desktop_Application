package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Util.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Util.ThemeManager;
import com.example.infection_monitoring_system_desktop_application.Util.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void login() {
        String email = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        User user = userDAO.loginUser(email, password);

        if (user == null) {
            showError("Invalid email or password.");
        } else {
            SceneManager.switchScene("/com/example/infection_monitoring_system_desktop_application/View/Dashboard.fxml");
        }
    }

    @FXML
    private void toggleTheme() {
        ThemeManager.toggleTheme();
        SceneManager.refreshCurrentScene();
    }

    @FXML
    private void toggleLanguage() {
        LanguageManager.toggleLanguage();
        SceneManager.refreshCurrentScene();
    }

    @FXML
    private void viewRegistration() {
        SceneManager.switchScene(
                "/com/example/infection_monitoring_system_desktop_application/View/Registration.fxml"
        );
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
