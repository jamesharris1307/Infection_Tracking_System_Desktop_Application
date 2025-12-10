package com.example.infection_monitoring_system_desktop_application.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomePageController {

    private GeneralPublicDashboardController parentController;

    @FXML
    private Button submitReportButton;
    @FXML Button updateMedicalHistoryButton;
    @FXML Button viewSubmissionsButton;

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML
    private void initialize() {
        submitReportButton.setOnAction(e -> {
            if (parentController != null) {
                parentController.showSubmitReportPage();
            }

            if (parentController != null) {
                parentController.showEditDetailsPage();
            }
        });

        updateMedicalHistoryButton.setOnAction(e -> {
            if (parentController != null) {
                parentController.showUpdateMedicalHistoryPage();
            }
        });

//         TODO Implement the View Submissions Page (After Refactor)
//        viewSubmissionsButton.setOnAction(e -> {
//            if (parentController != null) {
//                parentController.showViewSubmissionsPage();
//            }
//        });
    }

}
