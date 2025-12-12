package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.util.Locale;

class LanguageManagerTest {

    @BeforeEach void resetLanguage() throws PreferencesException {
        LanguageManager.setEnglish();
    }

    @Test void getBundle() {
        assertNotNull(LanguageManager.getBundle());
        assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
    }

    @Test void setEnglish() throws PreferencesException {
        LanguageManager.setLanguage(Locale.FRENCH);
        LanguageManager.setEnglish();
        assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
    }

    @Test void setWelsh() throws PreferencesException {
        LanguageManager.setWelsh();
        assertEquals("cy", LanguageManager.getBundle().getLocale().getLanguage());
    }

    @Test void applySavedLanguageEnglish() throws PreferencesException {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadLanguage).thenReturn("en");
            LanguageManager.applySavedLanguage();
            assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
        }
    }

    @Test void applySavedLanguageWelsh() throws PreferencesException {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadLanguage).thenReturn("cy");
            LanguageManager.applySavedLanguage();
            assertEquals("cy", LanguageManager.getBundle().getLocale().getLanguage());
        }
    }

    @Test void toggleLanguageFromEnglishToWelsh() throws PreferencesException {
        LanguageManager.setEnglish();
        assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
        LanguageManager.toggleLanguage();
        assertEquals("cy", LanguageManager.getBundle().getLocale().getLanguage());
    }

    @Test void toggleLanguageFromWelshToEnglish() throws PreferencesException {
        LanguageManager.setWelsh();
        assertEquals("cy", LanguageManager.getBundle().getLocale().getLanguage());
        LanguageManager.toggleLanguage();
        assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
    }
}