package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Util.ThemeManager;
import com.example.infection_monitoring_system_desktop_application.Util.LanguageManager;
import javafx.fxml.FXML;

public class LoginController {

    @FXML
    private void login() {

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
}
