package com.example.infection_monitoring_system_desktop_application.Manager;

import com.example.infection_monitoring_system_desktop_application.Controller.*;
import com.example.infection_monitoring_system_desktop_application.Service.*;
import com.example.infection_monitoring_system_desktop_application.Model.*;

public final class AppContext {

    private static AppContext instance;

    private final LegacyConnectionAdapter dataSource;

    private final UserService userService;
    private final CaseService caseService;
    private final MedicalHistoryService medicalHistoryService;

    private AppContext() {
        this.dataSource = new LegacyConnectionAdapter();
        UserDAO userDAO = new UserDAO(dataSource);
        CaseDAO caseDAO = new CaseDAO(dataSource);
        MedicalHistoryDAO medicalHistoryDAO = new MedicalHistoryDAO(dataSource);

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

    public Object getControllerInstance(Class<?> type) {
        if (type == AdministratorDashboardController.class) {
            AdministratorDashboardController c = new AdministratorDashboardController();
            c.setUserService(userService);
            return c;
        }

        else if (type == HealthcareProfessionalController.class) {
            HealthcareProfessionalController c = new HealthcareProfessionalController();
            c.setCaseService(caseService);
            return c;
        }

        else if (type == GeneralPublicDashboardController.class) {
            GeneralPublicDashboardController c = new GeneralPublicDashboardController();
            c.setUserService(userService);
            c.setCaseService(caseService);
            c.setMedicalHistoryService(medicalHistoryService);
            return c;
        }

        else if (type == LoginController.class) {
            LoginController c = new LoginController();
            c.setUserService(userService);
            return c;
        } else if (type == RegistrationController.class) {
            RegistrationController c = new RegistrationController();
            c.setUserService(userService);
            return c;
        } else if (type == EditDetailsController.class) {
            EditDetailsController c = new EditDetailsController();
            c.setUserService(userService);
            return c;
        } else if (type == UpdateMedicalHistoryController.class) {
            UpdateMedicalHistoryController c = new UpdateMedicalHistoryController();
            c.setMedicalHistoryService(medicalHistoryService);
            return c;
        }

        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate controller: " + type.getName(), e);
        }
    }
}