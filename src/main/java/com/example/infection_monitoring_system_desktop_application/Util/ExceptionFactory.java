package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.*;

public class ExceptionFactory {

    private ExceptionFactory() {}

    public static ValidationException validationError(String message) {
        return new ValidationException(message);
    }

    public static UserNotFoundException userNotFound(String email) {
        return new UserNotFoundException("User with email '" + email + "' not found.");
    }

    public static InvalidCredentialsException invalidPassword() {
        return new InvalidCredentialsException("Invalid password provided.");
    }

    public static DAOException databaseError(Throwable cause) {
        return new DAOException("Database operation failed.", cause);
    }

    public static DataNotFoundException dataNotFound(String message) {
        return new DataNotFoundException(message);
    }

    public static IllegalUserRoleException unknownRole(String role) {
        return new IllegalUserRoleException("Unknown user role: " + role);
    }

    public static AppException unexpected(Throwable cause) {
        return new AppException("Unexpected error occurred.", cause) {};
    }
}