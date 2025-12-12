package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;

import java.io.*;
import java.util.Properties;

public class UserPreferences {

    private static String FILE = "userprefs.properties";

    static void setFile(String filePath) {
        FILE = filePath;
    }

    public static void saveDarkMode(boolean darkMode) throws PreferencesException {
        Properties props = loadProperties();
        props.setProperty("darkMode", Boolean.toString(darkMode));
        storeProperties(props);
    }

    public static boolean loadDarkMode() throws PreferencesException {
        Properties props = loadProperties();
        return Boolean.parseBoolean(props.getProperty("darkMode", "false"));
    }

    public static void saveLanguage(String languageCode) throws PreferencesException {
        Properties props = loadProperties();
        props.setProperty("language", languageCode);
        storeProperties(props);
    }

    public static String loadLanguage() throws PreferencesException {
        Properties props = loadProperties();
        return props.getProperty("language", "en");
    }

    private static Properties loadProperties() throws PreferencesException {
        Properties props = new Properties();
        File file = new File(FILE);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new PreferencesException("Failed to create preferences file", e);
            }
        }

        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            throw new PreferencesException("Failed to load user preferences", e);
        }

        return props;
    }

    private static void storeProperties(Properties props) throws PreferencesException {
        try (OutputStream out = new FileOutputStream(FILE)) {
            props.store(out, "User Preferences");
        } catch (IOException e) {
            throw new PreferencesException("Failed to save user preferences", e);
        }
    }
}
