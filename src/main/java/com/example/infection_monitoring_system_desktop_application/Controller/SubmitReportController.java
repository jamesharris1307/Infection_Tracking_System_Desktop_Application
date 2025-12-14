package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Service.CaseService;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import com.example.infection_monitoring_system_desktop_application.Model.Case;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import javafx.application.Platform;
import java.time.LocalDateTime;
import javafx.scene.control.*;
import java.util.HashSet;
import javafx.fxml.FXML;
import java.util.Set;

public class SubmitReportController {
    private GeneralPublicDashboardController parentController;

    @FXML
    private RadioButton feverSymptomCheck;
    @FXML
    private RadioButton coughSymptomCheck;
    @FXML
    private RadioButton headacheSymptomCheck;
    @FXML
    private RadioButton fatigueSymptomCheck;
    @FXML
    private RadioButton shortnessOfBreathCheck;
    @FXML
    private RadioButton lossOfSmellCheck;
    @FXML
    private RadioButton MildSeverityCheck;
    @FXML
    private RadioButton ModerateSeverityCheck;
    @FXML
    private RadioButton SevereSeverityCheck;
    @FXML
    private RadioButton YesWorsenedSymptomCheck;
    @FXML
    private RadioButton NoWorsenedSymptomCheck;
    @FXML
    private DatePicker symptomsStartDate;
    @FXML
    private CheckBox exposureCheck;
    @FXML
    private Button backButton;

    private final ToggleGroup severityGroup = new ToggleGroup();
    private final ToggleGroup worsenedGroup = new ToggleGroup();

    private CaseService caseService;

    public void setCaseService(CaseService caseService) {
        this.caseService = caseService;
    }

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    @FXML
    private void initialize() {
        try {
            MildSeverityCheck.setToggleGroup(severityGroup);
            ModerateSeverityCheck.setToggleGroup(severityGroup);
            SevereSeverityCheck.setToggleGroup(severityGroup);

            YesWorsenedSymptomCheck.setToggleGroup(worsenedGroup);
            NoWorsenedSymptomCheck.setToggleGroup(worsenedGroup);
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error initializing case report form controls.");
        }
    }

    @FXML private void handleSubmitReport() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();

            if (currentUser == null || currentUser.getUserId() <= 0) {
                throw ExceptionFactory.validationError("No valid user is logged in. Please log in again.");
            }

            if (caseService == null) {
                throw ExceptionFactory.unexpected(new IllegalStateException("CaseService dependency not initialized."));
            }

            int userId = currentUser.getUserId();
            LocalDateTime dateReported = LocalDateTime.now();
            LocalDateTime symptomsBegan = symptomsStartDate.getValue() != null
                    ? symptomsStartDate.getValue().atStartOfDay()
                    : null;

            Set<String> symptomsSet = new HashSet<>();
            if (feverSymptomCheck.isSelected()) symptomsSet.add("Fever");
            if (coughSymptomCheck.isSelected()) symptomsSet.add("Cough");
            if (headacheSymptomCheck.isSelected()) symptomsSet.add("Headache");
            if (fatigueSymptomCheck.isSelected()) symptomsSet.add("Fatigue");
            if (shortnessOfBreathCheck.isSelected()) symptomsSet.add("ShortnessOfBreath");
            if (lossOfSmellCheck.isSelected()) symptomsSet.add("LossOfSmell");

            String severity = severityGroup.getSelectedToggle() != null
                    ? ((RadioButton) severityGroup.getSelectedToggle()).getText()
                    : null;

            if (severity == null || symptomsSet.isEmpty()) {
                throw ExceptionFactory.validationError("Please select at least one symptom and a severity level.");
            }

            boolean confirmedWorsened = YesWorsenedSymptomCheck.isSelected();
            Case newCase = new Case(userId, dateReported, symptomsBegan, symptomsSet, severity, confirmedWorsened);

            caseService.addCaseAsync(newCase)
                    .thenRun(() -> Platform.runLater(() -> {
                        AlertUtils.showInfo("Submitted", "Your case report has been submitted.");
                        clearForm();
                        if (parentController != null) {
                            parentController.showHomePage();
                        }
                    }))
                    .exceptionally(ex -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        ExceptionHandler.handle(cause, "Failed to submit case report asynchronously.");
                        return null;
                    });
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error submitting case report");
        }
    }

    private void clearForm() {
        feverSymptomCheck.setSelected(false);
        coughSymptomCheck.setSelected(false);
        headacheSymptomCheck.setSelected(false);
        fatigueSymptomCheck.setSelected(false);
        shortnessOfBreathCheck.setSelected(false);
        lossOfSmellCheck.setSelected(false);

        severityGroup.selectToggle(null);
        worsenedGroup.selectToggle(null);
        symptomsStartDate.setValue(null);
    }
}