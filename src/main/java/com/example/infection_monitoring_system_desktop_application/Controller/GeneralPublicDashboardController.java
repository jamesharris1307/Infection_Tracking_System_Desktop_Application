package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.SceneLoadException;
import com.example.infection_monitoring_system_desktop_application.Service.MedicalHistoryService;
import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Service.CaseService;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.time.LocalDate;
import javafx.scene.Node;
import javafx.fxml.FXML;

public class GeneralPublicDashboardController {

    public Label lblCurrentDate;
    public Label lblHistoryDate;
    public Label lblVaccStatus;
    public Label lblExistingConditions;
    public Label labelFullName;
    public Label labelEmail;

    @FXML private StackPane centerPages;
    @FXML private Button logoutButton;
    @FXML private Button settingsButton;
    @FXML private Button submitReportButton;

    private UserService userService;
    private CaseService caseService;
    private MedicalHistoryService medicalHistoryService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public void setCaseService(CaseService caseService) {
        this.caseService = caseService;
    }
    public void setMedicalHistoryService(MedicalHistoryService medicalHistoryService) {
        this.medicalHistoryService = medicalHistoryService;
    }

    @FXML private void initialize() {
        try {
            loadPage("/com/example/infection_monitoring_system_desktop_application/View/HomePage.fxml");
            setupButtons();
            loadPanelData();
        } catch (SceneLoadException e) {
            ExceptionHandler.handle(e, "CRITICAL: Failed to Load HomePage.fxml");
        }
    }

    private void setupButtons() {
        logoutButton.setOnAction(e -> handleLogout());
        settingsButton.setOnAction(e -> showSettingsPage());
    }

    private String yesNoOrDashVaccination(String value) {
        if (value == null) return "-";
        if (value.equals("1")) return "Up to Date";
        if (value.equals("0")) return "Not Up to Date";
        return "-";
    }

    private String yesNoOrDashExistingConditions(String value) {
        if (value == null) return "-";
        if (value.equals("1")) return "Yes";
        if (value.equals("0")) return "No";
        return "-";
    }

    private void loadPanelData() {
        int userId = SessionManager.getInstance().getCurrentUser().getUserId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        lblCurrentDate.setText(LocalDate.now().format(formatter));

        userService.getDashboardInfoAsync(userId)
                .thenAccept(info -> Platform.runLater(() -> {
                    if (info != null) {
                        lblHistoryDate.setText(info.getLastHistoryUpdate() != null
                                ? info.getLastHistoryUpdate().format(formatter)
                                : "-");
                        lblVaccStatus.setText(yesNoOrDashVaccination(info.getVaccinationStatus()));
                        lblExistingConditions.setText(yesNoOrDashExistingConditions(info.getExistingConditions()));
                        labelFullName.setText(info.getFirstName() + " " + info.getLastName());
                        labelEmail.setText(info.getEmail());
                    } else {
                        lblHistoryDate.setText("-");
                        lblVaccStatus.setText("-");
                        lblExistingConditions.setText("-");
                        labelFullName.setText("-");
                        labelEmail.setText("-");
                    }
                }))
                .exceptionally(ex -> {
                    lblHistoryDate.setText("-");
                    lblVaccStatus.setText("-");
                    lblExistingConditions.setText("-");
                    labelFullName.setText("-");
                    labelEmail.setText("-");
                    ExceptionHandler.handle(ex, "Failed to Load Panel Info");
                    return null;
                });
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxmlPath),
                    LanguageManager.getInstance().getBundle()
            );

            if (loader.getLocation() == null) {
                throw new SceneLoadException("Resource Not Found: " + fxmlPath);
            }

            Node page = loader.load();
            Object controller = loader.getController();

            if (controller instanceof HomePageController homeController) {
                homeController.setParentController(this);
            } else if (controller instanceof SubmitReportController submitController) {
                submitController.setParentController(this);
                submitController.setCaseService(this.caseService);
            } else if (controller instanceof UpdateMedicalHistoryController updateMedicalHistory) {
                updateMedicalHistory.setParentController(this);
                updateMedicalHistory.setMedicalHistoryService(this.medicalHistoryService);
            } else if (controller instanceof EditDetailsController editDetailsController) {
                editDetailsController.setParentController(this);
                editDetailsController.setUserService(this.userService);
            } else if (controller instanceof SettingsPageController settingsController) {
                settingsController.setParentController(this);
            }

            try {
                var field = controller.getClass().getDeclaredField("backButton");
                field.setAccessible(true);
                Object value = field.get(controller);
                if (value instanceof Button backBtn) {
                    setupBackButton(backBtn);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }

            centerPages.getChildren().clear();
            centerPages.getChildren().add(page);

        } catch (IOException e) {
            throw new SceneLoadException("Failed To Load FXML: " + fxmlPath, e);
        } catch (SceneLoadException e) {
            throw e;
        } catch (Exception ex) {
            throw ExceptionFactory.unexpected(ex);
        }
    }

    @FXML public void showHomePage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/HomePage.fxml");
    }
    @FXML public void showSubmitReportPage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/SubmitReport.fxml");
    }
    @FXML public void showUpdateMedicalHistoryPage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/UpdateMedicalHistory.fxml");
    }
    @FXML private void showSettingsPage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/SettingsPage.fxml");
    }
    @FXML public void showEditDetailsPage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/EditDetails.fxml");
    }

    public void setupBackButton(Button backButton) {
        if (backButton != null) {
            backButton.setOnAction(e -> showHomePage());
        }
    }

    private void handleLogout() {
        try {
            SessionManager.getInstance().clearSession();
            SceneManager.switchRoot(
                    "/com/example/infection_monitoring_system_desktop_application/View/Login.fxml"
            );
        } catch (Exception e) {
            ExceptionHandler.handle(e, "CRITICAL: Logout Failure");
        }
    }
}