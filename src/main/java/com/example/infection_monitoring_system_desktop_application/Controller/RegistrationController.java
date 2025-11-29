package com.example.infection_monitoring_system_desktop_application.Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class RegistrationController {

    @FXML
    private AnchorPane page1AccountDetails; // This matches fx:id in FXML

    @FXML
    private void initialize() {
        // Show page 1 when the FXML loads
        page1AccountDetails.setVisible(true);
    }
}

