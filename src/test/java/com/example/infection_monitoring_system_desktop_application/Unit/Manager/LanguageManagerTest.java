package com.example.infection_monitoring_system_desktop_application.Unit.Manager;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import static org.junit.jupiter.api.Assertions.*;

import com.example.infection_monitoring_system_desktop_application.Util.UserPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.util.Locale;

class LanguageManagerTest {

    @BeforeEach void resetLanguage() throws PreferencesException {
        LanguageManager.getInstance().setEnglish();
    }

    @Test void getBundle() {
        assertNotNull(LanguageManager.getInstance().getBundle());
        assertEquals("en", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
    }

    @Test void setEnglish() throws PreferencesException {
        LanguageManager.getInstance().setLanguage(Locale.FRENCH);
        LanguageManager.getInstance().setEnglish();
        assertEquals("en", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
    }

    @Test void setWelsh() throws PreferencesException {
        LanguageManager.getInstance().setWelsh();
        assertEquals("cy", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
    }

    @Test void applySavedLanguageEnglish() throws PreferencesException {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadLanguage).thenReturn("en");
            LanguageManager.getInstance().applySavedLanguage();
            assertEquals("en", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
        }
    }

    @Test void applySavedLanguageWelsh() throws PreferencesException {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadLanguage).thenReturn("cy");
            LanguageManager.getInstance().applySavedLanguage();
            assertEquals("cy", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
        }
    }

    @Test void toggleLanguageFromEnglishToWelsh() throws PreferencesException {
        LanguageManager.getInstance().setEnglish();
        assertEquals("en", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
        LanguageManager.getInstance().toggleLanguage();
        assertEquals("cy", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
    }

    @Test void toggleLanguageFromWelshToEnglish() throws PreferencesException {
        LanguageManager.getInstance().setWelsh();
        assertEquals("cy", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
        LanguageManager.getInstance().toggleLanguage();
        assertEquals("en", LanguageManager.getInstance().getBundle().getLocale().getLanguage());
    }
}