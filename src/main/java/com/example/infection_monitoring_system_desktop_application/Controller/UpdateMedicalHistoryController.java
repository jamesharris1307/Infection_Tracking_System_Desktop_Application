package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Service.MedicalHistoryService;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistory;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;
import java.time.LocalDateTime;
import javafx.fxml.FXML;

public class UpdateMedicalHistoryController {
    private GeneralPublicDashboardController parentController;

    @FXML private RadioButton longTermConditionsYes;
    @FXML private RadioButton longTermConditionsNo;
    @FXML private RadioButton longTermMedicationsYes;
    @FXML private RadioButton longTermMedicationsNo;
    @FXML private RadioButton upToDateVaccinationsYes;
    @FXML private RadioButton upToDateVaccinationsNo;
    @FXML private RadioButton allergiesYes;
    @FXML private RadioButton allergiesNo;
    @FXML private Button backButton;

    private final ToggleGroup longTermConditionsGroup = new ToggleGroup();
    private final ToggleGroup longTermMedicationsGroup = new ToggleGroup();
    private final ToggleGroup upToDateVaccinationsGroup = new ToggleGroup();
    private final ToggleGroup allergiesGroup = new ToggleGroup();
    public void setParentController(GeneralPublicDashboardController parent) {this.parentController = parent;}

    @FXML private void initialize() {
        longTermConditionsYes.setToggleGroup(longTermConditionsGroup);
        longTermConditionsNo.setToggleGroup(longTermConditionsGroup);
        longTermMedicationsYes.setToggleGroup(longTermMedicationsGroup);
        longTermMedicationsNo.setToggleGroup(longTermMedicationsGroup);
        upToDateVaccinationsYes.setToggleGroup(upToDateVaccinationsGroup);
        upToDateVaccinationsNo.setToggleGroup(upToDateVaccinationsGroup);
        allergiesYes.setToggleGroup(allergiesGroup);
        allergiesNo.setToggleGroup(allergiesGroup);
    }

    @FXML private void handleUpdateMedicalHistory() {
        try {
            if (SessionManager.getInstance().getCurrentUser() == null) {
                AlertUtils.showError("Error", "No user is currently logged in.");
                return;
            }

            int userId = SessionManager.getInstance().getCurrentUser().getUserId();
            boolean hasConditions = longTermConditionsYes.isSelected();
            boolean hasMedications = longTermMedicationsYes.isSelected();
            boolean vaccinationsUpToDate = upToDateVaccinationsYes.isSelected();
            boolean hasAllergies = allergiesYes.isSelected();

            LocalDateTime lastUpdated = LocalDateTime.now();
            MedicalHistory medicalHistory = new MedicalHistory(userId, hasConditions, hasMedications, vaccinationsUpToDate, hasAllergies, lastUpdated);
            MedicalHistoryService service = new MedicalHistoryService();

            service.getMedicalHistoryByUserIdAsync(userId)
                    .thenCompose(existing -> {
                        if (existing == null) {
                            return service.addMedicalHistoryAsync(medicalHistory);
                        } else {
                            return service.updateMedicalHistoryAsync(medicalHistory);
                        }
                    })
                    .thenRun(() -> javafx.application.Platform.runLater(() ->
                            AlertUtils.showInfo("Success", "Medical history updated successfully.")
                    ))
                    .exceptionally(ex -> {
                        javafx.application.Platform.runLater(() ->
                                AlertUtils.showError("Unexpected Error", "Failed to update medical history.")
                        );
                        return null;
                    });
        } catch (Exception e) {
            javafx.application.Platform.runLater(() ->
                    AlertUtils.showError("Unexpected Error", "An unexpected error occurred.")
            );
        }
    }
}