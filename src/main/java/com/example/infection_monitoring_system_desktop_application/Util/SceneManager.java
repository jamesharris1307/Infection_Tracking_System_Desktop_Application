package com.example.infection_monitoring_system_desktop_application.Util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage mainStage;

    public static void init(Stage stage) {
        mainStage = stage;
    }

    public static void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath),
                    LanguageManager.getBundle()
            );

            Parent root = loader.load();

            root.setUserData(fxmlPath);

            Scene scene = new Scene(root);

            ThemeManager.getInstance().applySavedTheme(scene);

            mainStage.setScene(scene);
            mainStage.show();

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(SceneManager.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Failed to load FXML: " + fxmlPath, e);
        }
    }

    public static void refreshCurrentScene() {
        if (mainStage == null || mainStage.getScene() == null) return;

        String fxmlPath = (String) mainStage.getScene().getRoot().getUserData();
        if (fxmlPath != null) {
            switchScene(fxmlPath);
        }
    }
}
