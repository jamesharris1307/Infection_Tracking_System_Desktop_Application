package com.example.infection_monitoring_system_desktop_application.Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class RegistrationController {

    @FXML
    public AnchorPane page1AccountDetails;

    @FXML
    private void initialize() {
        // Only show page1
        page1AccountDetails.setVisible(true);
    }
}
