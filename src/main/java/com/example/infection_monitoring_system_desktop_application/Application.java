package com.example.infection_monitoring_system_desktop_application;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Manager.AppContext;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override public void start(Stage stage) {
        try {
            AppContext.getInstance();
            LanguageManager.getInstance().applySavedLanguage();
            SceneManager.init(stage);
            SceneManager.switchRoot("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");

        } catch (PreferencesException e) {
            Throwable systemError = ExceptionFactory.unexpected(e);
            ExceptionHandler.handle(
                    systemError,
                    "FATAL: Application failed to load critical preferences (language/theme) during startup."
            );
            Platform.exit();
        }
    }
}



