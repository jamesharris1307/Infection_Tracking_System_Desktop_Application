package com.example.infection_monitoring_system_desktop_application.Unit.Util;

import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionFactoryTest {

    @Test
    void constructor_ShouldBePrivateAndThrowExceptionOnInvocation() throws Exception {
        Constructor<ExceptionFactory> constructor = ExceptionFactory.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                "ExceptionFactory constructor should be private.");

        constructor.setAccessible(true);

        ExceptionFactory instance = constructor.newInstance();
        assertNotNull(instance);
    }

    @Test
    void validationError_ShouldReturnValidationExceptionWithMessage() {
        String testMessage = "Input is missing.";
        ValidationException ex = ExceptionFactory.validationError(testMessage);

        assertInstanceOf(ValidationException.class, ex);
        assertEquals(testMessage, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void userNotFound_ShouldReturnUserNotFoundExceptionWithEmailInMessage() {
        String testEmail = "test@example.com";
        UserNotFoundException ex = ExceptionFactory.userNotFound(testEmail);

        assertInstanceOf(UserNotFoundException.class, ex);
        assertTrue(ex.getMessage().contains("User with email '" + testEmail + "' not found."),
                "Message should contain the input email.");
        assertNull(ex.getCause());
    }

    @Test
    void invalidPassword_ShouldReturnInvalidCredentialsException() {
        InvalidCredentialsException ex = ExceptionFactory.invalidPassword();

        assertInstanceOf(InvalidCredentialsException.class, ex);
        assertEquals("Invalid password provided.", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void databaseError_ShouldReturnDAOExceptionWrappingCause() {
        Throwable testCause = new RuntimeException("SQL failure");
        DAOException ex = ExceptionFactory.databaseError(testCause);

        assertInstanceOf(DAOException.class, ex);
        assertEquals("Database operation failed.", ex.getMessage());
        assertSame(testCause, ex.getCause(), "Exception should wrap the provided cause.");
    }

    @Test
    void dataNotFound_ShouldReturnDataNotFoundExceptionWithMessage() {
        String testMessage = "Case ID 123 not found.";
        DataNotFoundException ex = ExceptionFactory.dataNotFound(testMessage);

        assertInstanceOf(DataNotFoundException.class, ex);
        assertEquals(testMessage, ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void unknownRole_ShouldReturnIllegalUserRoleExceptionWithRoleInMessage() {
        String testRole = "SUPER_ADMIN";
        IllegalUserRoleException ex = ExceptionFactory.unknownRole(testRole);

        assertInstanceOf(IllegalUserRoleException.class, ex);
        assertTrue(ex.getMessage().contains("Unknown user role: " + testRole),
                "Message should contain the unknown role.");
        assertNull(ex.getCause());
    }

    @Test
    void unexpected_ShouldReturnAppExceptionWrappingCause() {
        Throwable testCause = new IOException("Network drop");
        AppException ex = ExceptionFactory.unexpected(testCause);

        assertInstanceOf(AppException.class, ex);
        assertEquals("Unexpected error occurred.", ex.getMessage());
        assertSame(testCause, ex.getCause(), "Exception should wrap the provided cause.");
    }
}