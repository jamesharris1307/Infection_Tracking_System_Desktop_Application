package com.example.infection_monitoring_system_desktop_application.Util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", Locale.ENGLISH);

    public static void setLanguage(Locale locale) {
        bundle = ResourceBundle.getBundle("i18n.messages", locale);
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }
}