package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Manager.ThemeManager;
import javafx.scene.control.Button;
import javafx.fxml.FXML;

public class SettingsPageController {
    private GeneralPublicDashboardController parentController;

    @FXML private Button editProfileButton;

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML private void initialize() {
        editProfileButton.setOnAction(e -> handleNavigation());
    }

    private void handleNavigation() {
        if (parentController == null) {
            ExceptionHandler.handle(
                    ExceptionFactory.unexpected(new IllegalStateException("Parent Controller Missing")),
                    "CRITICAL: Dependency Injection Failed"
            );
            return;
        }

        try {
            parentController.showEditDetailsPage();
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, "Failed to Show Edit Details Page");
        }
    }

    @FXML private void handleChangeTheme() {
        try {
            ThemeManager.getInstance().toggleTheme();
            SceneManager.refreshCurrentRoot();
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, "Failed to Apply New Theme");
        }
    }

    @FXML private void handleChangeLanguage() {
        try {
            LanguageManager.getInstance().toggleLanguage();
            SceneManager.refreshCurrentRoot();
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, "Failed Language Switch");
        }
    }
}