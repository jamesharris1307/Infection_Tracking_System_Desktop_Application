package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Service.MedicalHistoryService;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
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
    private MedicalHistoryService medicalHistoryService;

    public void setMedicalHistoryService(MedicalHistoryService medicalHistoryService) {
        this.medicalHistoryService = medicalHistoryService;
    }

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
                throw ExceptionFactory.validationError("No user is currently logged in. Please log in again.");
            }

            if (medicalHistoryService == null) {
                throw ExceptionFactory.unexpected(new IllegalStateException("MedicalHistoryService dependency not initialized."));
            }

            int userId = SessionManager.getInstance().getCurrentUser().getUserId();
            boolean hasConditions = longTermConditionsYes.isSelected();
            boolean hasMedications = longTermMedicationsYes.isSelected();
            boolean vaccinationsUpToDate = upToDateVaccinationsYes.isSelected();
            boolean hasAllergies = allergiesYes.isSelected();

            if (longTermConditionsGroup.getSelectedToggle() == null || longTermMedicationsGroup.getSelectedToggle() == null ||
                    upToDateVaccinationsGroup.getSelectedToggle() == null || allergiesGroup.getSelectedToggle() == null) {
                throw ExceptionFactory.validationError("Please make a selection for all medical history fields.");
            }

            LocalDateTime lastUpdated = LocalDateTime.now();
            MedicalHistory medicalHistory = new MedicalHistory(userId, hasConditions, hasMedications, vaccinationsUpToDate, hasAllergies, lastUpdated);

            medicalHistoryService.getMedicalHistoryByUserIdAsync(userId)
                    .thenCompose(existing -> {
                        if (existing == null) {
                            return medicalHistoryService.addMedicalHistoryAsync(medicalHistory);
                        } else {
                            medicalHistory.setMedicalHistoryID(existing.getMedicalHistoryID());
                            return medicalHistoryService.updateMedicalHistoryAsync(medicalHistory);
                        }
                    })
                    .thenRun(() -> javafx.application.Platform.runLater(() -> {
                        AlertUtils.showInfo("Success", "Medical history updated successfully.");
                        clearForm();
                        if (parentController != null) {
                            parentController.showHomePage();
                        }
                    }))
                    .exceptionally(ex -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        ExceptionHandler.handle(cause, "Failed to update medical history asynchronously.");
                        return null;
                    });
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error during medical history submission.");
        }
    }

    private void clearForm() {
        longTermConditionsGroup.selectToggle(null);
        longTermMedicationsGroup.selectToggle(null);
        upToDateVaccinationsGroup.selectToggle(null);
        allergiesGroup.selectToggle(null);
    }
}