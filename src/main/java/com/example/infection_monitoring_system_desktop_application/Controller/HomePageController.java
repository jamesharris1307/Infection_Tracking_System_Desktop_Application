package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
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
        submitReportButton.setOnAction(e -> handleNavigation(
                () -> parentController.showSubmitReportPage(),
                "Submit Report"
        ));

        updateMedicalHistoryButton.setOnAction(e -> handleNavigation(
                () -> parentController.showUpdateMedicalHistoryPage(),
                "Update Medical History"
        ));
    }

    private void handleNavigation(Runnable navigationAction, String pageName) {
        if (parentController == null) {
            ExceptionHandler.handle(
                    ExceptionFactory.unexpected(new IllegalStateException("Parent controller link is missing.")),
                    "CRITICAL: Cannot navigate to " + pageName + ". Dependency Injection failed."
            );
            return;
        }

        try {
            navigationAction.run();
        } catch (Exception ex) {
            ExceptionHandler.handle(ex, "Failed to navigate to " + pageName + " page.");
        }
    }
}