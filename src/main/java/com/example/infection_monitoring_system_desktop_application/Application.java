package com.example.infection_monitoring_system_desktop_application;

import com.example.infection_monitoring_system_desktop_application.Util.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Util.ThemeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        LanguageManager.applySavedLanguage();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml"),
                LanguageManager.getBundle() // <-- pass the bundle here
        );

        Parent root = loader.load();
        Scene scene = new Scene(root);

        ThemeManager.applySavedTheme(scene);

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

}
