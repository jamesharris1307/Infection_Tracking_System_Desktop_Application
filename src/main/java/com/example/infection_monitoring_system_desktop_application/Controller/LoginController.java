package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.IconLoader;
import com.example.infection_monitoring_system_desktop_application.Util.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML
    private void login(ActionEvent actionEvent) {
        // Your login logic here
    }

    @FXML
    private void toggleTheme(ActionEvent actionEvent) {
        System.out.println("Toggle clicked!");
        Scene scene = ((Node) actionEvent.getSource()).getScene();
        ThemeManager.toggleTheme(scene);
    }

    @FXML
    private void toggleLanguage(ActionEvent actionEvent) {
        System.out.println("Toggle clicked!");
    }

    @FXML
    private void viewRegistration(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/Registration.fxml")
            );
            Parent newRoot = loader.load();

            // Get the existing scene instead of making a new one
            Scene currentScene = ((javafx.scene.Node) actionEvent.getSource()).getScene();

            // Swap the root node
            currentScene.setRoot(newRoot);

            // Update window title if needed
            Stage stage = (Stage) currentScene.getWindow();
            stage.setTitle("Registration");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
