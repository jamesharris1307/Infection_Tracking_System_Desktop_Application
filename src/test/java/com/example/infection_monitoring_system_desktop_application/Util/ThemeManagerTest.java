package com.example.infection_monitoring_system_desktop_application.Util;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ThemeManagerTest {

    @BeforeAll
    static void initJfx() throws InterruptedException {
        final var latch = new java.util.concurrent.CountDownLatch(1);
        Platform.startup(latch::countDown);
        latch.await();
    }

    @BeforeEach
    void resetTheme() {
        if (ThemeManager.isDarkMode()) {
            ThemeManager.toggleTheme();
        } assertFalse(ThemeManager.isDarkMode());
    }

    @Test
    void toggleTheme() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            ThemeManager.toggleTheme();
            assertTrue(ThemeManager.isDarkMode());
            mockedPrefs.verify(() -> UserPreferences.saveDarkMode(true));
        }
    }

    @Test
    void applyThemeDarkMode() {
        Scene scene = new Scene(new StackPane());
        ThemeManager.toggleTheme();
        ThemeManager.applyTheme(scene);
        assertTrue(scene.getStylesheets().get(0).contains("darkMode.css"));
    }

    @Test
    void applyThemeLightMode() {
        Scene scene = new Scene(new StackPane());
        assertFalse(ThemeManager.isDarkMode());
        ThemeManager.applyTheme(scene);
        assertTrue(scene.getStylesheets().get(0).contains("lightMode.css"));
    }

    @Test
    void applySavedThemeLoadsDarkMode() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadDarkMode).thenReturn(true);
            Scene scene = new Scene(new StackPane());
            ThemeManager.applySavedTheme(scene);
            assertTrue(ThemeManager.isDarkMode());
            assertTrue(scene.getStylesheets().get(0).contains("darkMode.css"));
        }
    }

    @Test
    void applySavedThemeLoadsLightMode() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadDarkMode).thenReturn(false);
            Scene scene = new Scene(new StackPane());
            ThemeManager.applySavedTheme(scene);
            assertFalse(ThemeManager.isDarkMode());
            assertTrue(scene.getStylesheets().get(0).contains("lightMode.css"));
        }
    }

    @Test
    void isDarkModeReflectsState() {
        assertFalse(ThemeManager.isDarkMode());
        ThemeManager.toggleTheme();
        assertTrue(ThemeManager.isDarkMode());
        ThemeManager.toggleTheme();
        assertFalse(ThemeManager.isDarkMode());
    }
}
