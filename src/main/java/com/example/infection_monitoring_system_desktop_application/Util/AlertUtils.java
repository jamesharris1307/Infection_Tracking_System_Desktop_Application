package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Window;

public class AlertUtils {

    private AlertUtils() {
    }

    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Window owner = SceneManager.getMainStage();
        if (owner != null) {
            alert.initOwner(owner);
            alert.initModality(Modality.WINDOW_MODAL);
        }

        alert.showAndWait();
    }
}
