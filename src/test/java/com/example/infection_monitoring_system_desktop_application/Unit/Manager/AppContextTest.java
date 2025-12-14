package com.example.infection_monitoring_system_desktop_application.Unit.Manager;

import com.example.infection_monitoring_system_desktop_application.Controller.*;
import com.example.infection_monitoring_system_desktop_application.Manager.AppContext;
import com.example.infection_monitoring_system_desktop_application.Service.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AppContextTest {

    private static final String APP_CONTEXT_FIELD = "instance";
    public static class UnhandledController {}

    @BeforeEach
    @AfterEach
    void resetSingleton() throws Exception {
        Field instance = AppContext.class.getDeclaredField(APP_CONTEXT_FIELD);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void getInstance_ShouldReturnSameInstance() {
        AppContext instance1 = AppContext.getInstance();
        AppContext instance2 = AppContext.getInstance();
        assertSame(instance1, instance2, "AppContext should always return the same singleton instance.");
    }

    @Test
    void getInstance_ShouldInitializeAllCoreServices() throws NoSuchFieldException, IllegalAccessException {
        AppContext context = AppContext.getInstance();

        Field userServiceField = context.getClass().getDeclaredField("userService");
        userServiceField.setAccessible(true);
        UserService userService = (UserService) userServiceField.get(context);

        Field caseServiceField = context.getClass().getDeclaredField("caseService");
        caseServiceField.setAccessible(true);
        CaseService caseService = (CaseService) caseServiceField.get(context);

        assertNotNull(userService, "UserService should be initialized.");
        assertNotNull(caseService, "CaseService should be initialized.");
    }

    @Test
    void getControllerInstance_LoginControllerShouldReceiveUserService() throws NoSuchFieldException, IllegalAccessException {
        AppContext context = AppContext.getInstance();

        Field userServiceField = context.getClass().getDeclaredField("userService");
        userServiceField.setAccessible(true);
        UserService expectedService = (UserService) userServiceField.get(context);

        try (MockedConstruction<LoginController> mockConstruction = mockConstruction(LoginController.class)) {
            LoginController actualController = (LoginController) context.getControllerInstance(LoginController.class);
            verify(actualController, times(1)).setUserService(expectedService);
        }
    }


    @Test
    void getControllerInstance_HPCShouldReceiveCaseService() throws NoSuchFieldException, IllegalAccessException {
        AppContext context = AppContext.getInstance();
        Field caseServiceField = context.getClass().getDeclaredField("caseService");
        caseServiceField.setAccessible(true);
        CaseService expectedService = (CaseService) caseServiceField.get(context);

        try (MockedConstruction<HealthcareProfessionalController> mockConstruction = mockConstruction(HealthcareProfessionalController.class)) {
            HealthcareProfessionalController actualController = (HealthcareProfessionalController) context.getControllerInstance(HealthcareProfessionalController.class);
            verify(actualController, times(1)).setCaseService(expectedService);
        }
    }

    @Test
    void getControllerInstance_UnrecognizedControllerShouldUseDefaultFallback() {
        AppContext context = AppContext.getInstance();

        Object controller = context.getControllerInstance(UnhandledController.class);

        assertNotNull(controller);
        assertInstanceOf(UnhandledController.class, controller);
    }
}