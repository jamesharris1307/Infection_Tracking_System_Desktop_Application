package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.IllegalUserRoleException;
import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.ThemeManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class LoginController {

    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;

    private UserService userService;

    @FXML private void login() {
        String email = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertUtils.showError("Error", "Enter Email and Password.");
            return;
        }

        if (userService == null) {
            ExceptionHandler.handle(
                    ExceptionFactory.unexpected(new IllegalStateException("UserService Not Initialised.")),
                    "CRITICAL: UserService Missing from LoginController."
            );
            return;
        }

        userService.loginAsync(email, password)
                .thenAccept(user -> {
                    SessionManager.getInstance().setCurrentUser(user);
                    Platform.runLater(() -> switchDashboard(user.getRole()));
                })
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Login Attempt Failed.");
                    return null;
                });
    }

    private void switchDashboard(User.Role role) {
        switch (role) {
            case Administrator -> SceneManager.switchRoot(
                    "/com/example/infection_monitoring_system_desktop_application/View/AdministratorDashboard.fxml");
            case HealthcareProfessional -> SceneManager.switchRoot(
                    "/com/example/infection_monitoring_system_desktop_application/View/HealthcareProfessionalDashboard.fxml");
            case GeneralPublic -> SceneManager.switchRoot(
                    "/com/example/infection_monitoring_system_desktop_application/View/GeneralPublicDashboard.fxml");
            default -> {
                ExceptionHandler.handle(
                        ExceptionFactory.unexpected(new IllegalUserRoleException("Unknown User Role: " + role)),
                        "Invalid Dashboard Switch Attempt."
                );
            }
        }
    }

    @FXML private void toggleTheme() {
        try {
            ThemeManager.getInstance().toggleTheme();
            SceneManager.refreshCurrentRoot();
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Failed to Toggle Application Theme Preferences Error.");
        }
    }

    @FXML private void toggleLanguage() {
        try {
            LanguageManager.getInstance().toggleLanguage();
            SceneManager.refreshCurrentRoot();
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Failed to Toggle Application Language Preferences Error.");
        }
    }

    @FXML private void viewRegistration() {
        SceneManager.switchRoot(
                "/com/example/infection_monitoring_system_desktop_application/View/Registration.fxml"
        );
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}