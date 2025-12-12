package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;

import java.io.*;
import java.util.Properties;

public class UserPreferences {

    private static String FILE = "userprefs.properties";

    static void setFile(String filePath) {
        FILE = filePath;
    }

    public static void saveDarkMode(boolean darkMode) {
        Properties props = loadProperties();
        props.setProperty("darkMode", Boolean.toString(darkMode));
        storeProperties(props);
    }

    public static boolean loadDarkMode() {
        Properties props = loadProperties();
        return Boolean.parseBoolean(props.getProperty("darkMode", "false"));
    }

    public static void saveLanguage(String languageCode) {
        Properties props = loadProperties();
        props.setProperty("language", languageCode);
        storeProperties(props);
    }

    public static String loadLanguage() {
        Properties props = loadProperties();
        return props.getProperty("language", "en");
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(FILE)) {
            props.load(in);
        } catch (IOException ignored) {
        }
        return props;
    }

    private static void storeProperties(Properties props) {
        try (OutputStream out = new FileOutputStream(FILE)) {
            props.store(out, "User Preferences");
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(SceneManager.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Failed to save user preferences", e);
        }
    }
}
