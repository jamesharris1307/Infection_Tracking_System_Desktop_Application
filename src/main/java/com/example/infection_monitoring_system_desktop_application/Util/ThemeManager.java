package com.example.infection_monitoring_system_desktop_application.Util;

import java.util.Objects;

public class ThemeManager {

    private static boolean darkMode = false;

    public static void toggleTheme() {
        darkMode = !darkMode;
        UserPreferences.saveDarkMode(darkMode); // save the choice
    }

    public static void applyTheme(javafx.scene.Scene scene) {
        scene.getStylesheets().clear();
        String cssPath = darkMode
                ? Objects.requireNonNull(ThemeManager.class.getResource("/Styles/darkMode.css")).toExternalForm()
                : Objects.requireNonNull(ThemeManager.class.getResource("/Styles/lightMode.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);
    }

    public static void applySavedTheme(javafx.scene.Scene scene) {
        darkMode = UserPreferences.loadDarkMode();
        applyTheme(scene);
    }

    public static boolean isDarkMode() {
        return darkMode;
    }
}
