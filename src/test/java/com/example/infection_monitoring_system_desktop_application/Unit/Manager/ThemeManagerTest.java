package com.example.infection_monitoring_system_desktop_application.Unit.Manager;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import com.example.infection_monitoring_system_desktop_application.Manager.ThemeManager;
import static org.junit.jupiter.api.Assertions.*;

import com.example.infection_monitoring_system_desktop_application.Util.UserPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import javafx.scene.Scene;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class ThemeManagerTest {

    @BeforeAll
    static void initJfx() throws InterruptedException {
        if (Platform.isFxApplicationThread() || isPlatformRunning()) {
            return;
        }

        final var latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            latch.await(5, TimeUnit.SECONDS);
        } catch (IllegalStateException e) {
            System.err.println("JavaFX Platform was already initialized.");
        }
    }

    private static boolean isPlatformRunning() {
        try {
            Platform.runLater(() -> {});
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @BeforeEach void resetTheme() throws PreferencesException {
        if (ThemeManager.getInstance().isDarkMode()) {
            ThemeManager.getInstance().toggleTheme();
        }
        assertFalse(ThemeManager.getInstance().isDarkMode(), "Theme should be reset to Light Mode.");
    }

    @Test void toggleTheme() throws PreferencesException {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            ThemeManager.getInstance().toggleTheme();
            assertTrue(ThemeManager.getInstance().isDarkMode(), "Theme should be Dark Mode after toggle.");
            mockedPrefs.verify(() -> UserPreferences.saveDarkMode(true));
        }
    }

    @Test void applyThemeDarkMode() throws PreferencesException {
        Scene scene = new Scene(new StackPane());
        ThemeManager.getInstance().toggleTheme();
        ThemeManager.getInstance().applyTheme(scene);
        assertTrue(scene.getStylesheets().getFirst().contains("darkMode.css"), "Dark mode CSS should be applied.");
    }

    @Test void applyThemeLightMode() {
        Scene scene = new Scene(new StackPane());
        ThemeManager.getInstance().applyTheme(scene);
        assertTrue(scene.getStylesheets().getFirst().contains("lightMode.css"), "Light mode CSS should be applied.");
    }

    @Test void applySavedThemeLoadsDarkMode() throws PreferencesException {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadDarkMode).thenReturn(true);
            Scene scene = new Scene(new StackPane());
            ThemeManager.getInstance().applySavedTheme(scene);
            assertTrue(ThemeManager.getInstance().isDarkMode(), "State should reflect loaded Dark Mode preference.");
            assertTrue(scene.getStylesheets().getFirst().contains("darkMode.css"), "Dark mode CSS should be applied from saved preference.");
        }
    }

    @Test void applySavedThemeLoadsLightMode() {
        try (MockedStatic<UserPreferences> mockedPrefs = Mockito.mockStatic(UserPreferences.class)) {
            mockedPrefs.when(UserPreferences::loadDarkMode).thenReturn(false);
            Scene scene = new Scene(new StackPane());
            ThemeManager.getInstance().applySavedTheme(scene);
            assertFalse(ThemeManager.getInstance().isDarkMode(), "State should reflect loaded Light Mode preference.");
            assertTrue(scene.getStylesheets().getFirst().contains("lightMode.css"), "Light mode CSS should be applied from saved preference.");
        } catch (PreferencesException e) {
            fail("PreferencesException should not be thrown during mocked preference load.");
        }
    }

    @Test void isDarkModeReflectsState() throws PreferencesException {
        assertFalse(ThemeManager.getInstance().isDarkMode(), "Initial state must be false.");
        ThemeManager.getInstance().toggleTheme();
        assertTrue(ThemeManager.getInstance().isDarkMode(), "State should be true after first toggle.");
        ThemeManager.getInstance().toggleTheme();
        assertFalse(ThemeManager.getInstance().isDarkMode(), "State should be false after second toggle.");
    }
}