package com.example.infection_monitoring_system_desktop_application.Manager;

import com.example.infection_monitoring_system_desktop_application.Controller.*;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;
import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistoryDAO;
import com.example.infection_monitoring_system_desktop_application.Model.LegacyConnectionAdapter;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Service.CaseService;
import com.example.infection_monitoring_system_desktop_application.Service.MedicalHistoryService;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.SceneLoadException;
import javafx.scene.input.KeyCode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class SceneManager {

    private static Stage mainStage;
    private static Scene mainScene;

    private static final LegacyConnectionAdapter dataSource = new LegacyConnectionAdapter();

    private static final UserService userService = new UserService(new UserDAO(dataSource));
    private static final CaseService caseService = new CaseService(new CaseDAO(dataSource));
    private static final MedicalHistoryService medicalHistoryService = new MedicalHistoryService(new MedicalHistoryDAO(dataSource));

    public static void init(Stage stage) {
        mainStage = stage;
    }

    public static void switchRoot(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath),
                    LanguageManager.getBundle()
            );
            Parent root = loader.load();
            root.setUserData(fxmlPath);

            Object controller = loader.getController();

            if (controller instanceof LoginController) {
                ((LoginController) controller).setUserService(userService);
            }
            else if (controller instanceof RegistrationController) {
                ((RegistrationController) controller).setUserService(userService);
            }
            else if (controller instanceof EditDetailsController) {
                ((EditDetailsController) controller).setUserService(userService);
            }
            else if (controller instanceof AdministratorDashboardController) {
                ((AdministratorDashboardController) controller).setUserService(userService);
            }
            else if (controller instanceof GeneralPublicDashboardController) {
                GeneralPublicDashboardController dashboard = (GeneralPublicDashboardController) controller;
                dashboard.setUserService(userService);
                dashboard.setCaseService(caseService);
                dashboard.setMedicalHistoryService(medicalHistoryService);
            }
            else if (controller instanceof HealthcareProfessionalController) {
                ((HealthcareProfessionalController) controller).setCaseService(caseService);
            }

            if (mainScene == null) {
                mainScene = new Scene(root);
                ThemeManager.getInstance().applySavedTheme(mainScene);

                mainScene.setOnKeyPressed(event -> {
                    if (Objects.requireNonNull(event.getCode()) == KeyCode.ESCAPE) {
                        mainStage.close();
                    }
                });

                mainStage.setScene(mainScene);
                mainStage.show();
            } else {
                mainScene.setRoot(root);
                ThemeManager.getInstance().applySavedTheme(mainScene);
            }

            String title = deriveTitleFromFXML(fxmlPath);
            mainStage.setTitle(title);

        } catch (IOException | PreferencesException e) {
            throw new SceneLoadException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    private static String deriveTitleFromFXML(String fxmlPath) {
        String fileName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
        if (fileName.endsWith(".fxml")) fileName = fileName.replace(".fxml", "");
        return fileName + " - My Application";
    }

    public static void refreshCurrentRoot() {
        if (mainScene == null) return;

        String fxmlPath = (String) mainScene.getRoot().getUserData();
        if (fxmlPath != null) {
            switchRoot(fxmlPath);
        }
    }

    public static Stage getMainStage() {
        return mainStage;
    }
}