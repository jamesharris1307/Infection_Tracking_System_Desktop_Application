package com.example.infection_monitoring_system_desktop_application.Unit.Manager;

import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.AdministratorUser;
import com.example.infection_monitoring_system_desktop_application.Model.GeneralPublicUser;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private User testUserAdmin;
    private User testUserPublic;
    private final SessionManager manager = SessionManager.getInstance();

    @BeforeEach
    void setUp() {
        final LocalDate DUMMY_DOB = LocalDate.of(1990, 1, 1);
        final String DUMMY_ADDRESS = "1 Test St";
        final String DUMMY_TOWN = "Test Town";
        final String DUMMY_POSTCODE = "SW1A 0AA";

        testUserAdmin = new AdministratorUser(
                "admin@ims.com",
                "hashed_p",
                "Admin",
                "User",
                DUMMY_DOB,
                DUMMY_ADDRESS,
                "",
                DUMMY_TOWN,
                "County",
                DUMMY_POSTCODE,
                User.AccountStatus.Active
        );

        testUserPublic = new GeneralPublicUser(
                "public@ims.com",
                "hashed_p",
                "Public",
                "User",
                DUMMY_DOB,
                DUMMY_ADDRESS,
                "",
                DUMMY_TOWN,
                "County",
                DUMMY_POSTCODE,
                User.AccountStatus.Active
        );

        manager.clearSession();
        assertNull(manager.getCurrentUser(), "Session must be empty before each test.");
    }

    @Test
    void singleton_ReturnsTheIdenticalInstance() {
        SessionManager instance1 = SessionManager.getInstance();
        SessionManager instance2 = SessionManager.getInstance();

        assertSame(instance1, instance2, "All calls to getInstance() must return the same unique object.");
    }

    @Test
    void setCurrentUser_CorrectlyStoresUserForSession() {
        manager.setCurrentUser(testUserAdmin);
        User retrievedUser = manager.getCurrentUser();

        assertNotNull(retrievedUser, "Current user should not be null after setting.");
        assertEquals(User.Role.Administrator, retrievedUser.getRole(),
                "The stored user must be the correct concrete type.");
    }

    @Test
    void clearSession_ResetsCurrentUserToNull() {
        manager.setCurrentUser(testUserPublic);
        assertNotNull(manager.getCurrentUser());

        manager.clearSession();
        assertNull(manager.getCurrentUser(), "Current user must be null after clearSession().");
    }

    @Test
    void singletonState_IsMaintainedAndConsistentAcrossReferences() {
        SessionManager managerA = SessionManager.getInstance();
        SessionManager managerB = SessionManager.getInstance();
        managerA.setCurrentUser(testUserAdmin);

        assertEquals(testUserAdmin, managerB.getCurrentUser(),
                "State set by A must be consistent and visible in B.");

        managerB.clearSession();
        assertNull(managerA.getCurrentUser(),
                "Clearance by B must be globally visible in A.");
    }
}