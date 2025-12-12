package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.io.File;

class UserPreferencesTest {

    private static File tempFile;

    @BeforeAll static void setup() throws Exception {
        tempFile = File.createTempFile("userprefs_test", ".properties");
        tempFile.deleteOnExit();
        Field fileField = UserPreferences.class.getDeclaredField("FILE");
        fileField.setAccessible(true);
        fileField.set(null, tempFile.getAbsolutePath());
    }

    @BeforeEach void cleanFile() {
        tempFile.delete();
    }

    @Test void saveAndLoadDarkMode() throws PreferencesException {
        assertFalse(UserPreferences.loadDarkMode());
        UserPreferences.saveDarkMode(true);
        assertTrue(UserPreferences.loadDarkMode());
        UserPreferences.saveDarkMode(false);
        assertFalse(UserPreferences.loadDarkMode());
    }

    @Test void saveAndLoadLanguage() throws PreferencesException {
        assertEquals("en", UserPreferences.loadLanguage());
        UserPreferences.saveLanguage("cy");
        assertEquals("cy", UserPreferences.loadLanguage());
        UserPreferences.saveLanguage("en");
        assertEquals("en", UserPreferences.loadLanguage());
    }
}