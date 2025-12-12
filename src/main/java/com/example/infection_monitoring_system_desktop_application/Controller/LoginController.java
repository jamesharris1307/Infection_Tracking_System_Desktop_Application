package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Manager.ThemeManager;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    private final UserService userService = new UserService();

    @FXML
    private void login() {
        String email = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertUtils.showError("Error","Please enter both email and password.");
            return;
        }

        userService.getUserByEmailAsync(email)
                .thenAcceptAsync(user -> {
                    if (user == null || !PasswordUtils.checkPassword(password, user.getPassword())) {
                        Platform.runLater(() ->
                                AlertUtils.showError("Error","Invalid email or password.")
                        );
                        return;
                    }

                    Platform.runLater(() -> {
                        SessionManager.getInstance().setCurrentUser(user);
                        User.Role role = user.getRole();

                        switch (role) {
                            case Administrator -> SceneManager.switchRoot(
                                    "/com/example/infection_monitoring_system_desktop_application/View/AdministratorDashboard.fxml");
                            case HealthcareProfessional -> SceneManager.switchRoot(
                                    "/com/example/infection_monitoring_system_desktop_application/View/HealthcareProfessionalDashboard.fxml");
                            case GeneralPublic -> SceneManager.switchRoot(
                                    "/com/example/infection_monitoring_system_desktop_application/View/GeneralPublicDashboard.fxml");
                            default -> AlertUtils.showError("Error","Unknown user role.");
                        }
                    });

                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() ->
                            AlertUtils.showError("Error","An error occurred during login.")
                    );
                    return null;
                });
    }

    @FXML
    private void toggleTheme() {
        ThemeManager.getInstance().toggleTheme();
        SceneManager.refreshCurrentRoot();
    }

    @FXML
    private void toggleLanguage() {
        LanguageManager.toggleLanguage();
        SceneManager.refreshCurrentRoot();
    }

    @FXML
    private void viewRegistration() {
        SceneManager.switchRoot(
                "/com/example/infection_monitoring_system_desktop_application/View/Registration.fxml"
        );
    }
}
