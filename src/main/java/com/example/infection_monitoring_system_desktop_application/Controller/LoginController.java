package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Util.*;
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
            AlertUtils.showError("Error","Please enter both email and password.");
            return;
        }

        User user = userDAO.getUserByEmail(email);
        if (user == null || !PasswordUtils.checkPassword(password, user.getPassword())) {
            AlertUtils.showError("Error","Invalid email or password.");
            return;
        }
        SessionManager.getInstance().setCurrentUser(user);
        SceneManager.switchScene("/com/example/infection_monitoring_system_desktop_application/View/GeneralPublicDashboard.fxml");
    }

    @FXML
    private void toggleTheme() {
        ThemeManager.getInstance().toggleTheme();
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
}
