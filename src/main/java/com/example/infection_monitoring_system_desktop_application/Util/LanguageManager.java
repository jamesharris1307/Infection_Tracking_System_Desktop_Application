package com.example.infection_monitoring_system_desktop_application.Util;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static ResourceBundle bundle =
            ResourceBundle.getBundle("i18n.messages", Locale.ENGLISH);

    public static ResourceBundle getBundle() {
        return bundle;
    }

    /** Set language and save preference */
    public static void setLanguage(Locale locale) {
        bundle = ResourceBundle.getBundle("i18n.messages", locale);
        UserPreferences.saveLanguage(locale.getLanguage());
    }

    public static void setEnglish() {
        setLanguage(Locale.ENGLISH);
    }

    public static void setWelsh() {
        setLanguage(new Locale("cy"));
    }

    /** Load the saved language on startup */
    public static void applySavedLanguage() {
        String code = UserPreferences.loadLanguage();
        if ("cy".equals(code)) {
            setWelsh();
        } else {
            setEnglish();
        }
    }

    /** Toggle language for the next refresh */
    public static void toggleLanguage() {
        String current = bundle.getLocale().getLanguage();
        if ("en".equals(current)) {
            setWelsh();
        } else {
            setEnglish();
        }
    }
}
