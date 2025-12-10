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

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML
    private void initialize() {
        longTermConditionsYes.setToggleGroup(longTermConditionsGroup);
        longTermConditionsNo.setToggleGroup(longTermConditionsGroup);

        longTermMedicationsYes.setToggleGroup(longTermMedicationsGroup);
        longTermMedicationsNo.setToggleGroup(longTermMedicationsGroup);

        upToDateVaccinationsYes.setToggleGroup(upToDateVaccinationsGroup);
        upToDateVaccinationsNo.setToggleGroup(upToDateVaccinationsGroup);

        allergiesYes.setToggleGroup(allergiesGroup);
        allergiesNo.setToggleGroup(allergiesGroup);
    }

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
