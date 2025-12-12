package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class LanguageManagerTest {

    @BeforeEach
    void resetLanguage() {
        LanguageManager.setEnglish();
    }

    @Test
    void getBundle() {
        assertNotNull(LanguageManager.getBundle());
        assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
    }

//    @Test
//    void setLanguageToFrench() {
//        LanguageManager.setLanguage(Locale.FRENCH);
//        assertEquals("fr", LanguageManager.getBundle().getLocale().getLanguage());
//    }

    @Test
    void setEnglish() {
        LanguageManager.setLanguage(Locale.FRENCH);
        LanguageManager.setEnglish();
        assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
    }

    @Test
    void setWelsh() {
        LanguageManager.setWelsh();
        assertEquals("cy", LanguageManager.getBundle().getLocale().getLanguage());
    }

    @Test
    void applySavedLanguageEnglish() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadLanguage).thenReturn("en");
            LanguageManager.applySavedLanguage();
            assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
        }
    }

    @Test
    void applySavedLanguageWelsh() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadLanguage).thenReturn("cy");
            LanguageManager.applySavedLanguage();
            assertEquals("cy", LanguageManager.getBundle().getLocale().getLanguage());
        }
    }

    @Test
    void toggleLanguageFromEnglishToWelsh() {
        LanguageManager.setEnglish();
        assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
        LanguageManager.toggleLanguage();
        assertEquals("cy", LanguageManager.getBundle().getLocale().getLanguage());
    }

    @Test
    void toggleLanguageFromWelshToEnglish() {
        LanguageManager.setWelsh();
        assertEquals("cy", LanguageManager.getBundle().getLocale().getLanguage());
        LanguageManager.toggleLanguage();
        assertEquals("en", LanguageManager.getBundle().getLocale().getLanguage());
    }
}
