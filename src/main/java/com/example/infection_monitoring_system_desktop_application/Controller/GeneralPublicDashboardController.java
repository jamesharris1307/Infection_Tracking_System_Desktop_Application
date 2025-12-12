package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class GeneralPublicDashboardController {

    @FXML private StackPane rootScaler;
    @FXML private AnchorPane rootContent;

    @FXML private StackPane centerPages;

    @FXML private Button logoutButton;
    @FXML private Button settingsButton;
    @FXML private Button submitReportButton;

    @FXML
    private void initialize() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/HomePage.fxml");
        setupButtons();
    }

    private void setupButtons() {
        logoutButton.setOnAction(e -> handleLogout());
        settingsButton.setOnAction(e -> showSettingsPage());
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node page = loader.load();

            Object controller = loader.getController();

            if (controller instanceof HomePageController homeController) {
                homeController.setParentController(this);
            } else if (controller instanceof SubmitReportController submitController) {
                submitController.setParentController(this);
            } else if (controller instanceof UpdateMedicalHistoryController updateMedicalHistory) {
                updateMedicalHistory.setParentController(this);
            } else if (controller instanceof EditDetailsController editDetailsController) {
                editDetailsController.setParentController(this);
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
                // This is optional and non-critical, so no alert
            }

            centerPages.getChildren().clear();
            centerPages.getChildren().add(page);

        } catch (IOException e) {
            AlertUtils.showError("Load Error", "Failed to load page: " + fxmlPath);
        } catch (Exception ex) {
            AlertUtils.showError("Unexpected Error", "An unexpected error occurred.");
        }
    }


    @FXML
    public void showHomePage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/HomePage.fxml");
    }

    @FXML
    public void showSubmitReportPage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/SubmitReport.fxml");
    }

    @FXML
    public void showUpdateMedicalHistoryPage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/UpdateMedicalHistory.fxml");
    }

    @FXML
    private void showSettingsPage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/SettingsPage.fxml");
    }

    @FXML
    public void showEditDetailsPage() {
        loadPage("/com/example/infection_monitoring_system_desktop_application/View/EditDetails.fxml");
    }

    public void setupBackButton(Button backButton) {
        if (backButton != null) {
            backButton.setOnAction(e -> showHomePage());
        }
    }

    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        SceneManager.switchRoot(
                "/com/example/infection_monitoring_system_desktop_application/View/Login.fxml"
        );
    }
}
