package com.example.infection_monitoring_system_desktop_application.Manager;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import com.example.infection_monitoring_system_desktop_application.Util.UserPreferences;
import java.util.ResourceBundle;
import java.util.Locale;

public class LanguageManager {

    private static final LanguageManager instance = new LanguageManager();

    private ResourceBundle bundle =
            ResourceBundle.getBundle("i18n.messages", Locale.ENGLISH);

    private LanguageManager() {}

    public static LanguageManager getInstance() {
        return instance;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setLanguage(Locale locale) throws PreferencesException {
        bundle = ResourceBundle.getBundle("i18n.messages", locale);
        UserPreferences.saveLanguage(locale.getLanguage());
    }

    public void setEnglish() throws PreferencesException {
        setLanguage(Locale.ENGLISH);
    }

    public void setWelsh() throws PreferencesException {
        setLanguage(new Locale("cy"));
    }

    public void applySavedLanguage() throws PreferencesException {
        String code = UserPreferences.loadLanguage();
        if ("cy".equals(code)) {
            setWelsh();
        } else {
            setEnglish();
        }
    }

    public void toggleLanguage() throws PreferencesException {
        String current = bundle.getLocale().getLanguage();
        if ("en".equals(current)) {
            setWelsh();
        } else {
            setEnglish();
        }
    }
}