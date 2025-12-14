package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.*;
import javafx.application.Platform;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());
    private static boolean loggingEnabled = true;

    private ExceptionHandler() {}

    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
    }

    public static void handle(Throwable e, String contextMessage) {
        if (loggingEnabled) {
            LOGGER.log(Level.SEVERE, contextMessage, e);
        }

        Platform.runLater(() -> {
            if (e instanceof ValidationException) {
                AlertUtils.showError("Input Error", e.getMessage());
            } else if (e instanceof UserNotFoundException || e instanceof InvalidCredentialsException) {
                AlertUtils.showError("Login Failed", e.getMessage());
            } else if (e instanceof DAOException) {
                AlertUtils.showError("Error", "Database error. Please try again later.");
            } else if (e instanceof IllegalUserRoleException) {
                AlertUtils.showError("Error", "User role not recognized.");
            } else {
                AlertUtils.showError("Error", "Unexpected error occurred.");
            }
        });
    }
}