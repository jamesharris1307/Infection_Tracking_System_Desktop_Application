package com.example.infection_monitoring_system_desktop_application;

import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws PreferencesException {
        LanguageManager.applySavedLanguage();
        SceneManager.init(stage);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH);
        SceneManager.switchRoot("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
    }
}
