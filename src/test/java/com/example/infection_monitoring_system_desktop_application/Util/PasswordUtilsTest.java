package com.example.infection_monitoring_system_desktop_application.Util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PasswordUtilsTest {

    @Test void hashPassword_generatesDifferentHashesForSameInput() {
        String password = "MySecretPassword";

        String hash1 = PasswordUtils.hashPassword(password);
        String hash2 = PasswordUtils.hashPassword(password);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2, "Hashes should be different due to random salt");
    }

    @Test void checkPassword_returnsTrueForCorrectPassword() {
        String password = "MySecretPassword";
        String hashed = PasswordUtils.hashPassword(password);

        assertTrue(PasswordUtils.checkPassword(password, hashed));
    }

    @Test void checkPassword_returnsFalseForIncorrectPassword() {
        String password = "MySecretPassword";
        String hashed = PasswordUtils.hashPassword(password);

        assertFalse(PasswordUtils.checkPassword("WrongPassword", hashed));
    }

    @Test void hashAndCheckPassword_workTogether() {
        String password = "AnotherPassword123!";
        String hashed = PasswordUtils.hashPassword(password);

        assertTrue(PasswordUtils.checkPassword(password, hashed));
        assertFalse(PasswordUtils.checkPassword(password + "x", hashed));
    }
}