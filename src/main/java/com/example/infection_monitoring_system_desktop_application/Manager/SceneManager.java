package com.example.infection_monitoring_system_desktop_application.Manager;

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

    public static void init(Stage stage) {
        mainStage = stage;
    }

    public static void switchRoot(Parent root) {
        try {
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

            String fxmlPath = (String) root.getUserData();
            String title = deriveTitleFromFXML(fxmlPath);
            mainStage.setTitle(title);

        } catch (PreferencesException e) {
            throw new SceneLoadException("Failed to apply saved theme or update scene root.", e);
        }
    }

    public static void switchRoot(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath),
                    LanguageManager.getInstance().getBundle()
            );

            loader.setControllerFactory(AppContext.getInstance()::getControllerInstance);

            Parent root = loader.load();
            root.setUserData(fxmlPath);

            switchRoot(root);

        } catch (IOException e) {
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