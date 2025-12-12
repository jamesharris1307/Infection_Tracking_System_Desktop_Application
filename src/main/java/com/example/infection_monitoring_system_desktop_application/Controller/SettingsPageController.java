package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Manager.ThemeManager;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import javafx.scene.control.Button;
import javafx.fxml.FXML;

public class SettingsPageController {
    private GeneralPublicDashboardController parentController;

    @FXML private Button editProfileButton;

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML private void initialize() {
        editProfileButton.setOnAction(e -> {
            try {
                if (parentController != null) {
                    parentController.showEditDetailsPage();
                } else {
                    AlertUtils.showError("Navigation Error", "Cannot open Edit Details page. Parent controller is missing.");
                }
            } catch (Exception ex) {
                AlertUtils.showError("Unexpected Error", "Failed to open Edit Details page.");
            }
        });
    }

    @FXML private void handleChangeTheme() {
        try {
            ThemeManager.getInstance().toggleTheme();
            SceneManager.refreshCurrentRoot();
        } catch (Exception ex) {
            AlertUtils.showError("Unexpected Error", "Failed to apply new theme.");
        }
    }

    @FXML private void handleChangeLanguage() {
        try {
            LanguageManager.toggleLanguage();
            SceneManager.refreshCurrentRoot();
        } catch (Exception ex) {
            AlertUtils.showError("Unexpected Error", "Failed to switch language.");
        }
    }
}