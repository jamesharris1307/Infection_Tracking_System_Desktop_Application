package com.example.infection_monitoring_system_desktop_application.Util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static ResourceBundle bundle =
            ResourceBundle.getBundle("i18n.messages", Locale.ENGLISH);

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void setLanguage(Locale locale) {
        bundle = ResourceBundle.getBundle("i18n.messages", locale);
        UserPreferences.saveLanguage(locale.getLanguage());
    }

    public static void setLanguageENG() {
        setLanguage(Locale.ENGLISH);
    }

    public static void setLanguageCY() {
        setLanguage(new Locale("cy"));
    }

    public static void applySavedLanguage() {
        String code = UserPreferences.loadLanguage();

        if (code.equals("cy")) {
            setLanguageCY();
        } else {
            setLanguageENG();
        }
    }

    public static void toggleLanguage(Node sourceNode, String fxmlPath) {

        String current = bundle.getLocale().getLanguage();
        String next = current.equals("en") ? "cy" : "en";

        setLanguage(Locale.forLanguageTag(next));

        try {
            FXMLLoader loader = new FXMLLoader(
                    LanguageManager.class.getResource(fxmlPath),
                    bundle
            );

            Parent root = loader.load();

            Scene scene = sourceNode.getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
