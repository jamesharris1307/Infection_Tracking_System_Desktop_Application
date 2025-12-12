package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Manager.ThemeManager;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
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
    void resetTheme() throws PreferencesException {
        if (ThemeManager.getInstance().isDarkMode()) {
            ThemeManager.getInstance().toggleTheme();
        } assertFalse(ThemeManager.getInstance().isDarkMode());
    }

    @Test
    void toggleTheme() throws PreferencesException {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            ThemeManager.getInstance().toggleTheme();
            assertTrue(ThemeManager.getInstance().isDarkMode());
            mockedPrefs.verify(() -> UserPreferences.saveDarkMode(true));
        }
    }

    @Test
    void applyThemeDarkMode() throws PreferencesException {
        Scene scene = new Scene(new StackPane());
        ThemeManager.getInstance().toggleTheme();
        ThemeManager.getInstance().applyTheme(scene);
        assertTrue(scene.getStylesheets().getFirst().contains("darkMode.css"));
    }

    @Test
    void applyThemeLightMode() {
        Scene scene = new Scene(new StackPane());
        assertFalse(ThemeManager.getInstance().isDarkMode());
        ThemeManager.getInstance().applyTheme(scene);
        assertTrue(scene.getStylesheets().getFirst().contains("lightMode.css"));
    }

    @Test
    void applySavedThemeLoadsDarkMode() throws PreferencesException {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadDarkMode).thenReturn(true);
            Scene scene = new Scene(new StackPane());
            ThemeManager.getInstance().applySavedTheme(scene);
            assertTrue(ThemeManager.getInstance().isDarkMode());
            assertTrue(scene.getStylesheets().getFirst().contains("darkMode.css"));
        }
    }

    @Test
    void applySavedThemeLoadsLightMode() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadDarkMode).thenReturn(false);
            Scene scene = new Scene(new StackPane());
            ThemeManager.getInstance().applySavedTheme(scene);
            assertFalse(ThemeManager.getInstance().isDarkMode());
            assertTrue(scene.getStylesheets().getFirst().contains("lightMode.css"));
        } catch (PreferencesException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isDarkModeReflectsState() throws PreferencesException {
        assertFalse(ThemeManager.getInstance().isDarkMode());
        ThemeManager.getInstance().toggleTheme();
        assertTrue(ThemeManager.getInstance().isDarkMode());
        ThemeManager.getInstance().toggleTheme();
        assertFalse(ThemeManager.getInstance().isDarkMode());
    }
}
