package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import javafx.scene.control.Button;
import javafx.fxml.FXML;

public class HomePageController {
    private GeneralPublicDashboardController parentController;

    @FXML private Button submitReportButton;
    @FXML private Button updateMedicalHistoryButton;
    @FXML private Button viewSubmissionsButton;

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML private void initialize() {
        submitReportButton.setOnAction(e -> {
            try {
                if (parentController != null) {
                    parentController.showSubmitReportPage();
                } else {
                    AlertUtils.showError("Navigation Error", "Cannot open page. Parent controller is missing.");
                }
            } catch (Exception ex) {
                AlertUtils.showError("Unexpected Error", "Failed to navigate to Submit Report page.");
            }
        });

        updateMedicalHistoryButton.setOnAction(e -> {
            try {
                if (parentController != null) {
                    parentController.showUpdateMedicalHistoryPage();
                } else {
                    AlertUtils.showError("Navigation Error", "Cannot open page. Parent controller is missing.");
                }
            } catch (Exception ex) {
                AlertUtils.showError("Unexpected Error", "Failed to navigate to Update Medical History page.");
            }
        });
    }
}