package com.example.infection_monitoring_system_desktop_application.Manager;

import com.example.infection_monitoring_system_desktop_application.Controller.*;
import com.example.infection_monitoring_system_desktop_application.Model.*;
import com.example.infection_monitoring_system_desktop_application.Service.*;

/**
 * AppContext acts as the central Factory and Service Locator.
 * It initializes all services and handles the dependency injection for FXML controllers.
 */
public final class AppContext {

    private static AppContext instance;

    // --- Core Dependencies ---
    private final LegacyConnectionAdapter dataSource;

    // --- Services (Singletons) ---
    private final UserService userService;
    private final CaseService caseService;
    private final MedicalHistoryService medicalHistoryService;

    private AppContext() {
        // 1. Initialize DAOs/Resources
        this.dataSource = new LegacyConnectionAdapter();
        UserDAO userDAO = new UserDAO(dataSource);
        CaseDAO caseDAO = new CaseDAO(dataSource);
        MedicalHistoryDAO medicalHistoryDAO = new MedicalHistoryDAO(dataSource);

        // 2. Initialize Services (Injection happens here!)
        this.userService = new UserService(userDAO);
        this.caseService = new CaseService(caseDAO);
        this.medicalHistoryService = new MedicalHistoryService(medicalHistoryDAO);
    }

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    // --- The Factory Method for Controllers (Controller Factory) ---
    /**
     * Called by FXMLLoader to retrieve a controller instance.
     * This method handles all service injection.
     */
    public Object getControllerInstance(Class<?> type) {
        // --- Dashboard Controllers ---
        if (type == AdministratorDashboardController.class) {
            AdministratorDashboardController c = new AdministratorDashboardController();
            c.setUserService(userService);
            // Add other services needed by Admin here!
            return c;
        }

        // This is the controller you provided the code for:
        else if (type == HealthcareProfessionalController.class) {
            HealthcareProfessionalController c = new HealthcareProfessionalController();
            c.setCaseService(caseService);
            // NOTE: If this controller needs UserService, add c.setUserService(userService) and the setter in the controller.
            return c;
        }

        // Use this block if your FXML refers to a class named HealthcareProfessionalDashboardController
        // else if (type == HealthcareProfessionalDashboardController.class) {
        //     HealthcareProfessionalDashboardController c = new HealthcareProfessionalDashboardController();
        //     c.setCaseService(caseService);
        //     c.setUserService(userService);
        //     return c;
        // }

        else if (type == GeneralPublicDashboardController.class) {
            GeneralPublicDashboardController c = new GeneralPublicDashboardController();
            c.setUserService(userService);
            c.setCaseService(caseService);
            c.setMedicalHistoryService(medicalHistoryService);
            return c;
        }

        // --- Utility/Form Controllers ---
        else if (type == LoginController.class) {
            LoginController c = new LoginController();
            c.setUserService(userService); // Login needs UserService
            return c;
        } else if (type == RegistrationController.class) {
            RegistrationController c = new RegistrationController();
            c.setUserService(userService); // Registration needs UserService
            return c;
        } else if (type == EditDetailsController.class) {
            EditDetailsController c = new EditDetailsController();
            c.setUserService(userService); // Edit Details needs UserService
            return c;
        } else if (type == UpdateMedicalHistoryController.class) {
            UpdateMedicalHistoryController c = new UpdateMedicalHistoryController();
            c.setMedicalHistoryService(medicalHistoryService); // Needs Medical History Service
            return c;
        }

        // --- Default creation for simple controllers that require no services ---
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate controller: " + type.getName(), e);
        }
    }
}