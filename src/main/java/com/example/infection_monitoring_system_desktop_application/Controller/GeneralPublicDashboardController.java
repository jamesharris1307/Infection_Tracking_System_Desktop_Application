package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Service.CaseService;
import com.example.infection_monitoring_system_desktop_application.Service.MedicalHistoryService;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.SceneLoadException;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.Node;
import javafx.fxml.FXML;

public class GeneralPublicDashboardController {

    @FXML private StackPane rootScaler;
    @FXML private AnchorPane rootContent;
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
        } catch (SceneLoadException e) {
            ExceptionHandler.handle(e, "CRITICAL: Failed to load initial dashboard page.");
        }
    }

    private void setupButtons() {
        logoutButton.setOnAction(e -> handleLogout());
        settingsButton.setOnAction(e -> showSettingsPage());
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (loader.getLocation() == null) {
                throw new SceneLoadException("Resource not found: " + fxmlPath);
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
            throw new SceneLoadException("Failed to load FXML: " + fxmlPath, e);
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
            ExceptionHandler.handle(e, "CRITICAL: Failed to log out or switch to login screen.");
        }
    }
}