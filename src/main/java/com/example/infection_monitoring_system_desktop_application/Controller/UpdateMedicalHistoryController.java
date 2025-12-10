package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistory;
import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistoryDAO;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDateTime;

public class UpdateMedicalHistoryController{

    private GeneralPublicDashboardController parentController;

    @FXML private ToggleGroup longTermConditionsGroup;
    @FXML private ToggleGroup longTermMedicationsGroup;
    @FXML private ToggleGroup upToDateVaccinationsGroup;
    @FXML private ToggleGroup allergiesGroup;

    @FXML private RadioButton longTermConditionsYes;
    @FXML private RadioButton longTermMedicationsYes;
    @FXML private RadioButton upToDateVaccinationsYes;
    @FXML private RadioButton allergiesYes;

    @FXML private Button backButton;

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML
    private void initialize() {}

    @FXML
    private void handleUpdateMedicalHistory() {
        try {
            boolean hasConditions = longTermConditionsYes.isSelected();
            boolean hasMedications = longTermMedicationsYes.isSelected();
            boolean vaccinationsUpToDate = upToDateVaccinationsYes.isSelected();
            boolean hasAllergies = allergiesYes.isSelected();
            LocalDateTime lastUpdated = LocalDateTime.now();

            User currentUser = SessionManager.getInstance().getCurrentUser();
            int userId = currentUser.getUserId();

            MedicalHistory medicalHistory = new MedicalHistory(
                    userId,
                    hasConditions,
                    hasMedications,
                    vaccinationsUpToDate,
                    hasAllergies,
                    lastUpdated
            );

            MedicalHistoryDAO medicalHistoryDAO = new MedicalHistoryDAO();
            medicalHistoryDAO.addMedicalHistory(medicalHistory);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
