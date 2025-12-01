package com.example.infection_monitoring_system_desktop_application;

import com.example.infection_monitoring_system_desktop_application.Util.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Util.SceneManager;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        LanguageManager.applySavedLanguage(); // Load user-saved language
        SceneManager.init(stage);
        SceneManager.switchScene("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
    }

}
