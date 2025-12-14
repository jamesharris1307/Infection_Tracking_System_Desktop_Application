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

    // DELETED: Static Service Initialization Block (Moved to AppContext)

    public static void init(Stage stage) {
        mainStage = stage;
    }

    // ADDED: Overloaded method to switch using a pre-loaded Parent (Handles PreferencesException)
    public static void switchRoot(Parent root) {
        try {
            if (mainScene == null) {
                mainScene = new Scene(root);
                ThemeManager.getInstance().applySavedTheme(mainScene); // throws PreferencesException

                mainScene.setOnKeyPressed(event -> {
                    if (Objects.requireNonNull(event.getCode()) == KeyCode.ESCAPE) {
                        mainStage.close();
                    }
                });

                mainStage.setScene(mainScene);
                mainStage.show();
            } else {
                mainScene.setRoot(root);
                ThemeManager.getInstance().applySavedTheme(mainScene); // throws PreferencesException
            }

            String fxmlPath = (String) root.getUserData();
            String title = deriveTitleFromFXML(fxmlPath);
            mainStage.setTitle(title);

        } catch (PreferencesException e) {
            // If the theme fails to load, we wrap it in an unchecked exception
            // to prevent propagating checked exceptions up the call stack,
            // which is good practice for critical initialization failures.
            throw new SceneLoadException("Failed to apply saved theme or update scene root.", e);
        }
    }


    // REVISED: switchRoot(String fxmlPath) (Handles only IOException)
    public static void switchRoot(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource(fxmlPath),
                    LanguageManager.getBundle()
            );

            // CRITICAL FIX: Integrate the Factory for dependency injection
            loader.setControllerFactory(AppContext.getInstance()::getControllerInstance);

            Parent root = loader.load(); // Throws IOException
            root.setUserData(fxmlPath);

            // Now call the overloaded method. The exceptions thrown inside
            // switchRoot(Parent root) are unchecked (SceneLoadException), so no catch needed here.
            switchRoot(root);

        } catch (IOException e) { // <-- ONLY catching IOException (thrown by loader.load())
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