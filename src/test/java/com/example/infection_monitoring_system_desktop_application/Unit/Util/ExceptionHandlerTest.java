package com.example.infection_monitoring_system_desktop_application.Unit.Util;

import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.*;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.mockito.Mockito.*;

class ExceptionHandlerTest {

    private MockedStatic<Platform> platformMock;
    private MockedStatic<AlertUtils> alertUtilsMock;
    private Logger mockLogger;

    private static final Logger REAL_LOGGER = Logger.getLogger(ExceptionHandler.class.getName());

    @BeforeEach
    void setUp() throws Exception {
        mockLogger = mock(Logger.class);
        ExceptionHandler.setLogger(mockLogger);
        ExceptionHandler.setLoggingEnabled(true);

        platformMock = mockStatic(Platform.class);
        platformMock.when(() -> Platform.runLater(any(Runnable.class)))
                .thenAnswer(invocation -> {
                    Runnable runnable = invocation.getArgument(0);
                    runnable.run();
                    return null;
                });
        alertUtilsMock = mockStatic(AlertUtils.class);
    }

    @AfterEach
    void tearDown() {
        platformMock.close();
        alertUtilsMock.close();
        ExceptionHandler.setLogger(REAL_LOGGER);
        ExceptionHandler.setLoggingEnabled(true);
    }


    @Test
    void handle_ShouldLogWhenLoggingIsEnabled() {
        Throwable testException = new RuntimeException("Test Error");
        String context = "Test context message";
        ExceptionHandler.handle(testException, context);
        verify(mockLogger, times(1)).log(Level.SEVERE, context, testException);
    }

    @Test
    void handle_ShouldNotLogWhenLoggingIsDisabled() {
        ExceptionHandler.setLoggingEnabled(false);
        Throwable testException = new RuntimeException("Test Error");
        String context = "Test context message";
        ExceptionHandler.handle(testException, context);
        verify(mockLogger, never()).log(any(Level.class), anyString(), any(Throwable.class));
        verify(mockLogger, never()).log(any(Level.class), anyString(), any(Object[].class));
    }

    @Test
    void handle_ShouldWrapUiUpdateInRunLater() {
        Throwable testException = new RuntimeException("Test Error");
        ExceptionHandler.handle(testException, "Context");
        platformMock.verify(() -> Platform.runLater(any(Runnable.class)), times(1));
    }

    @Test
    void handle_ValidationExceptionShouldShowInputErrorAlert() {
        String validationMsg = "Field X is required.";
        ValidationException ex = new ValidationException(validationMsg);
        ExceptionHandler.handle(ex, "Validation context");
        alertUtilsMock.verify(() -> AlertUtils.showError("Input Error", validationMsg), times(1));
        alertUtilsMock.verify(() -> AlertUtils.showError(anyString(), anyString()), times(1));
    }

    @Test
    void handle_UserNotFoundExceptionShouldShowLoginFailedAlert() {
        String errorMsg = "User not found.";
        UserNotFoundException ex = new UserNotFoundException(errorMsg);
        ExceptionHandler.handle(ex, "Login context");
        alertUtilsMock.verify(() -> AlertUtils.showError("Login Failed", errorMsg), times(1));
    }

    @Test
    void handle_DAOExceptionShouldShowGenericDatabaseErrorAlert() {
        DAOException ex = new DAOException("SQL syntax error", new SQLException("Bad SQL"));
        ExceptionHandler.handle(ex, "DAO context");
        alertUtilsMock.verify(() -> AlertUtils.showError("Error", "Database error. Please try again later."), times(1));
    }

    @Test
    void handle_IllegalUserRoleExceptionShouldShowRoleNotRecognizedAlert() {
        IllegalUserRoleException ex = new IllegalUserRoleException("Admin");
        ExceptionHandler.handle(ex, "Role context");
        alertUtilsMock.verify(() -> AlertUtils.showError("Error", "User role not recognized."), times(1));
    }

    @Test
    void handle_UnhandledRuntimeExceptionShouldShowUnexpectedErrorAlert() {
        RuntimeException ex = new RuntimeException("Uncaught logic error");
        ExceptionHandler.handle(ex, "Logic context");
        alertUtilsMock.verify(() -> AlertUtils.showError("Error", "Unexpected error occurred."), times(1));
    }
}