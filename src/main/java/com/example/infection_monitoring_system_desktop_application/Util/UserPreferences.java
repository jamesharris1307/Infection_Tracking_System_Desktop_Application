package com.example.infection_monitoring_system_desktop_application.Util;

import java.io.*;
import java.util.Properties;

public class UserPreferences {

    private static final String FILE = "userprefs.properties";

    // ---------- DARK MODE ----------
    public static void saveDarkMode(boolean darkMode) {
        Properties props = loadProperties();
        props.setProperty("darkMode", Boolean.toString(darkMode));
        storeProperties(props);
    }

    public static boolean loadDarkMode() {
        Properties props = loadProperties();
        return Boolean.parseBoolean(props.getProperty("darkMode", "false"));
    }

    // ---------- LANGUAGE ----------
    public static void saveLanguage(String languageCode) {
        Properties props = loadProperties();
        props.setProperty("language", languageCode);
        storeProperties(props);
    }

    public static String loadLanguage() {
        Properties props = loadProperties();
        // Default = English
        return props.getProperty("language", "en");
    }

    // ---------- HELPERS ----------
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(FILE)) {
            props.load(in);
        } catch (IOException ignored) {
            // file does not yet exist → return empty properties
        }
        return props;
    }

    private static void storeProperties(Properties props) {
        try (OutputStream out = new FileOutputStream(FILE)) {
            props.store(out, "User Preferences");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
