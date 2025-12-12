package com.example.infection_monitoring_system_desktop_application.Manager;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import com.example.infection_monitoring_system_desktop_application.Util.UserPreferences;
import javafx.scene.Scene;
import java.util.Objects;

public class ThemeManager {

    private static final ThemeManager instance = new ThemeManager();

    private boolean darkMode;

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        return instance;
    }

    public void toggleTheme() throws PreferencesException {
        darkMode = !darkMode;
        UserPreferences.saveDarkMode(darkMode);
    }

    public void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String cssPath = darkMode
                ? Objects.requireNonNull(getClass().getResource("/Styles/darkMode.css")).toExternalForm()
                : Objects.requireNonNull(getClass().getResource("/Styles/lightMode.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);
    }

    public void applySavedTheme(Scene scene) throws PreferencesException {
        darkMode = UserPreferences.loadDarkMode();
        applyTheme(scene);
    }

    public boolean isDarkMode() {
        return darkMode;
    }
}
