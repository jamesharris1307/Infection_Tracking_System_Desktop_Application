package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Util.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Util.ThemeManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SettingsPageController {

    private GeneralPublicDashboardController parentController;

    @FXML
    private Button editProfileButton;

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML
    private void initialize() {
        editProfileButton.setOnAction(e -> {
            if (parentController != null) {
                parentController.showEditDetailsPage();
            }
        });
    }

    @FXML
    private void handleChangeTheme() {
        ThemeManager.getInstance().toggleTheme();
        SceneManager.refreshCurrentRoot();
    }

    @FXML
    private void handleChangeLanguage() {
        LanguageManager.toggleLanguage();
        SceneManager.refreshCurrentRoot();
    }
}
