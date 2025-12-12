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
        if (ThemeManager.getInstance().isDarkMode()) {
            ThemeManager.getInstance().toggleTheme();
        } assertFalse(ThemeManager.getInstance().isDarkMode());
    }

    @Test
    void toggleTheme() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            ThemeManager.getInstance().toggleTheme();
            assertTrue(ThemeManager.getInstance().isDarkMode());
            mockedPrefs.verify(() -> UserPreferences.saveDarkMode(true));
        }
    }

    @Test
    void applyThemeDarkMode() {
        Scene scene = new Scene(new StackPane());
        ThemeManager.getInstance().toggleTheme();
        ThemeManager.getInstance().applyTheme(scene);
        assertTrue(scene.getStylesheets().get(0).contains("darkMode.css"));
    }

    @Test
    void applyThemeLightMode() {
        Scene scene = new Scene(new StackPane());
        assertFalse(ThemeManager.getInstance().isDarkMode());
        ThemeManager.getInstance().applyTheme(scene);
        assertTrue(scene.getStylesheets().get(0).contains("lightMode.css"));
    }

    @Test
    void applySavedThemeLoadsDarkMode() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadDarkMode).thenReturn(true);
            Scene scene = new Scene(new StackPane());
            ThemeManager.getInstance().applySavedTheme(scene);
            assertTrue(ThemeManager.getInstance().isDarkMode());
            assertTrue(scene.getStylesheets().get(0).contains("darkMode.css"));
        }
    }

    @Test
    void applySavedThemeLoadsLightMode() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadDarkMode).thenReturn(false);
            Scene scene = new Scene(new StackPane());
            ThemeManager.getInstance().applySavedTheme(scene);
            assertFalse(ThemeManager.getInstance().isDarkMode());
            assertTrue(scene.getStylesheets().get(0).contains("lightMode.css"));
        }
    }

    @Test
    void isDarkModeReflectsState() {
        assertFalse(ThemeManager.getInstance().isDarkMode());
        ThemeManager.getInstance().toggleTheme();
        assertTrue(ThemeManager.getInstance().isDarkMode());
        ThemeManager.getInstance().toggleTheme();
        assertFalse(ThemeManager.getInstance().isDarkMode());
    }
}
