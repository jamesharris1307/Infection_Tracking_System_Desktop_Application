package com.example.infection_monitoring_system_desktop_application.Util;

import javafx.scene.Scene;

import java.util.Objects;

public class ThemeManager {

    private static boolean darkMode = false;

    public static void setDarkTheme(Scene scene) {
        scene.getStylesheets().clear();
        String css = Objects.requireNonNull(ThemeManager.class.getResource("/Styles/darkMode.css")).toExternalForm();
        scene.getStylesheets().add(css);
        darkMode = true;
    }

    public static void setLightTheme(Scene scene) {
        scene.getStylesheets().clear();
        String css = Objects.requireNonNull(ThemeManager.class.getResource("/Styles/lightMode.css")).toExternalForm();
        scene.getStylesheets().add(css);
        darkMode = false;
    }

    public static void toggleTheme(Scene scene) {
        if (darkMode) {
            setLightTheme(scene);
        } else {
            setDarkTheme(scene);
        }
        UserPreferences.saveDarkMode(darkMode); // save the choice
    }

    public static void applySavedTheme(Scene scene) {
        if (UserPreferences.loadDarkMode()) {
            setDarkTheme(scene);
        } else {
            setLightTheme(scene);
        }
    }

    public static boolean isDarkMode() {
        return darkMode;
    }
}
