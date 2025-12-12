package com.example.infection_monitoring_system_desktop_application.Manager;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.SceneLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private static Stage mainStage;
    private static Scene mainScene;

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

        } catch (IOException | PreferencesException e) {
            throw new SceneLoadException("Failed to load FXML: " + fxmlPath, e);
        }
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
