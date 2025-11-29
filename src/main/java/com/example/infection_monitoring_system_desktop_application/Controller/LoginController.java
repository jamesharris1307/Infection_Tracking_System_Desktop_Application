package com.example.infection_monitoring_system_desktop_application.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private void login(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        System.out.println("Username entered: " + username);
    }

    @FXML
    private void viewRegistration(ActionEvent actionEvent) {
        try {
            // Load Registration.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/Registration.fxml"));
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage)((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            // Set new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Registration");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
