package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.Case;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import com.example.infection_monitoring_system_desktop_application.Util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubmitReportController {

    private GeneralPublicDashboardController parentController;

    @FXML private RadioButton feverSymptomCheck;
    @FXML private RadioButton coughSymptomCheck;
    @FXML private RadioButton headacheSymptomCheck;
    @FXML private RadioButton fatigueSymptomCheck;
    @FXML private RadioButton shortnessOfBreathCheck;
    @FXML private RadioButton lossOfSmellCheck;

    @FXML private RadioButton MildSeverityCheck;
    @FXML private RadioButton ModerateSeverityCheck;
    @FXML private RadioButton SevereSeverityCheck;

    @FXML private RadioButton YesWorsenedSymptomCheck;
    @FXML private RadioButton NoWorsenedSymptomCheck;

    private ToggleGroup severityGroup = new ToggleGroup();
    private ToggleGroup worsenedGroup = new ToggleGroup();

    @FXML private DatePicker symptomsStartDate;
    @FXML private CheckBox exposureCheck;

    @FXML private Button backButton;

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML
    private void initialize() {
        MildSeverityCheck.setToggleGroup(severityGroup);
        ModerateSeverityCheck.setToggleGroup(severityGroup);
        SevereSeverityCheck.setToggleGroup(severityGroup);

        YesWorsenedSymptomCheck.setToggleGroup(worsenedGroup);
        NoWorsenedSymptomCheck.setToggleGroup(worsenedGroup);
    }

    @FXML
    private void handleSubmitReport() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null || currentUser.getUserId() <= 0) {
                AlertUtils.showError("Error", "No valid user logged in.");
                return;
            }
            int userId = currentUser.getUserId();

            LocalDateTime dateReported = LocalDateTime.now();
            LocalDateTime symptomsBegan = symptomsStartDate.getValue() != null
                    ? symptomsStartDate.getValue().atStartOfDay()
                    : null;

            List<String> symptomsList = new ArrayList<>();
            if (feverSymptomCheck.isSelected()) symptomsList.add("Fever");
            if (coughSymptomCheck.isSelected()) symptomsList.add("Cough");
            if (headacheSymptomCheck.isSelected()) symptomsList.add("Headache");
            if (fatigueSymptomCheck.isSelected()) symptomsList.add("Fatigue");
            if (shortnessOfBreathCheck.isSelected()) symptomsList.add("ShortnessOfBreath");
            if (lossOfSmellCheck.isSelected()) symptomsList.add("LossOfSmell");

            Set<String> symptomsSet = new HashSet<>(symptomsList);

            String severity = severityGroup.getSelectedToggle() != null
                    ? ((RadioButton) severityGroup.getSelectedToggle()).getText()
                    : null;

            boolean confirmedWorsened = YesWorsenedSymptomCheck.isSelected();

            Case newCase = new Case(
                    userId,
                    dateReported,
                    symptomsBegan,
                    symptomsSet,
                    severity,
                    confirmedWorsened
            );

            CaseDAO caseDAO = new CaseDAO();
            caseDAO.addCase(newCase);

            AlertUtils.showInfo("Submitted", "Your case report has been submitted.");

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Error", "Failed to submit case report.");
        }
    }
}
