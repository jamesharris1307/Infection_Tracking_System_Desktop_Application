package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Util.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @FXML
    private void login(ActionEvent actionEvent) {
        // Your login logic here
    }

    @FXML
    private void toggleTheme(ActionEvent actionEvent) {
        Scene scene = ((Node) actionEvent.getSource()).getScene();
        ThemeManager.toggleTheme(scene);
    }

    @FXML
    private void toggleLanguage(ActionEvent actionEvent) {
        LanguageManager.toggleLanguage(
                (Node) actionEvent.getSource(),
                "/com/example/infection_monitoring_system_desktop_application/View/Login.fxml"
        );
    }


    @FXML
    private void viewRegistration(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/Registration.fxml")
            );
            Parent newRoot = loader.load();

            Scene currentScene = ((Node) actionEvent.getSource()).getScene();
            currentScene.setRoot(newRoot);

            Stage stage = (Stage) currentScene.getWindow();
            stage.setTitle("Registration");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load Registration.fxml", e);
        }
    }
}
