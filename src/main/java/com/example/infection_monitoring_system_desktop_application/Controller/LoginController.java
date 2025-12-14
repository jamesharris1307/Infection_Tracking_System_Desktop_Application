package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Manager.ThemeManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.*;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Util.*;
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
            AlertUtils.showError("Error", "Please enter both email and password.");
            return;
        }

        if (userService == null) {
            ExceptionHandler.handle(new IllegalStateException("UserService dependency not initialized."),
                    "CRITICAL: UserService is missing in LoginController.");
            return;
        }

        userService.getUserByEmailAsync(email)
                .thenAcceptAsync(user -> {
                    try {
                        if (user == null) throw new UserNotFoundException("User not found for email: " + email);
                        if (!PasswordUtils.checkPassword(password, user.getPassword()))
                            throw new InvalidCredentialsException("Invalid password");
                        SessionManager.getInstance().setCurrentUser(user);
                        Platform.runLater(() -> switchDashboard(user.getRole()));
                    } catch (Exception e) {
                        ExceptionHandler.handle(e, "Error validating user credentials");
                    }
                })
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Error fetching user asynchronously");
                    return null;
                });
    }

    private void switchDashboard(User.Role role) {
        try {
            switch (role) {
                case Administrator -> SceneManager.switchRoot(
                        "/com/example/infection_monitoring_system_desktop_application/View/AdministratorDashboard.fxml");
                case HealthcareProfessional -> SceneManager.switchRoot(
                        "/com/example/infection_monitoring_system_desktop_application/View/HealthcareProfessionalDashboard.fxml");
                case GeneralPublic -> SceneManager.switchRoot(
                        "/com/example/infection_monitoring_system_desktop_application/View/GeneralPublicDashboard.fxml");
                default -> throw new IllegalUserRoleException("Unknown user role: " + role);
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error switching dashboard for role: " + role);
        }
    }

    @FXML private void toggleTheme() {
        try {
            ThemeManager.getInstance().toggleTheme();
            SceneManager.refreshCurrentRoot();
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error toggling theme");
        }
    }

    @FXML private void toggleLanguage() {
        try {
            LanguageManager.toggleLanguage();
            SceneManager.refreshCurrentRoot();
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error toggling language");
        }
    }

    @FXML private void viewRegistration() {
        try {
            SceneManager.switchRoot(
                    "/com/example/infection_monitoring_system_desktop_application/View/Registration.fxml"
            );
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error switching to registration view");
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}