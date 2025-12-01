package com.example.infection_monitoring_system_desktop_application.Util;

import java.io.*;
import java.util.Properties;

public class UserPreferences {

    private static final String FILE = "userprefs.properties";

    public static void saveDarkMode(boolean darkMode) {
        Properties props = new Properties();
        props.setProperty("darkMode", Boolean.toString(darkMode));

        try (OutputStream out = new FileOutputStream(FILE)) {
            props.store(out, "User Preferences");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean loadDarkMode() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(FILE)) {
            props.load(in);
            return Boolean.parseBoolean(props.getProperty("darkMode", "false"));
        } catch (IOException e) {
            // File not found → default to light mode
            return false;
        }
    }
}
