package com.example.infection_monitoring_system_desktop_application.Integration;

import com.example.infection_monitoring_system_desktop_application.Controller.UpdateMedicalHistoryController;
import com.example.infection_monitoring_system_desktop_application.Service.MedicalHistoryService;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Model.*;
import org.testfx.framework.junit5.ApplicationTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeoutException;
import org.testfx.util.WaitForAsyncUtils;
import javafx.scene.control.RadioButton;
import java.util.concurrent.TimeUnit;
import org.h2.jdbcx.JdbcDataSource;
import java.util.ResourceBundle;
import org.junit.jupiter.api.*;
import javafx.fxml.FXMLLoader;
import javax.sql.DataSource;
import javafx.scene.Parent;
import java.time.LocalDate;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Statement;

public class UpdateMedicalHistoryControllerTest extends ApplicationTest {

    private UpdateMedicalHistoryController controller;
    private MedicalHistoryService medicalHistoryService;
    private MedicalHistoryDAO medicalHistoryDAO;
    private UserDAO userDAO;
    private DataSource h2DataSource;
    private int testUserId;

    private void createSQLSchema() {
        try (java.sql.Connection conn = h2DataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS MedicalHistory");
            stmt.execute("DROP TABLE IF EXISTS Users");

            stmt.execute("""
                CREATE TABLE Users (
                    UserID INT AUTO_INCREMENT PRIMARY KEY,
                    Email VARCHAR(255) NOT NULL UNIQUE,
                    Password VARCHAR(255) NOT NULL,
                    FirstName VARCHAR(255),
                    LastName VARCHAR(255),
                    DateOfBirth DATE,
                    AddressLine1 VARCHAR(255),
                    AddressLine2 VARCHAR(255),
                    TownCity VARCHAR(255),
                    County VARCHAR(255),
                    Postcode VARCHAR(20),
                    AccountStatus VARCHAR(50),
                    Role VARCHAR(50)
                )
            """);

            stmt.execute("""
                CREATE TABLE MedicalHistory (
                    UserID INT PRIMARY KEY,
                    LongTermConditions BOOLEAN,
                    LongTermMedications BOOLEAN,
                    vaccinationUpToDate BOOLEAN,
                    Allergies BOOLEAN,
                    LastUpdated TIMESTAMP,
                    FOREIGN KEY (UserID) REFERENCES USERS(UserID)
                )
            """);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }

    private void insertTestUser() {
        User user = new GeneralPublicUser(
                "history_test@example.com",
                PasswordUtils.hashPassword("password123"),
                "History", "User",
                LocalDate.of(1990, 5, 15),
                "1 Test St", "Apt 2", "Testville", "TestCounty", "T1 T2T",
                User.AccountStatus.Active
        );
        userDAO.addUser(user);

        User savedUser = userDAO.getUserByEmail(user.getEmail());
        testUserId = savedUser.getUserId();
        SessionManager.getInstance().setCurrentUser(savedUser);
    }

    @Override public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/UpdateMedicalHistory.fxml"),
                bundle
        );
        Parent root = loader.load();
        controller = loader.getController();

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb_med_history;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        this.h2DataSource = ds;
        createSQLSchema();

        userDAO = new UserDAO(this.h2DataSource);
        medicalHistoryDAO = new MedicalHistoryDAO(this.h2DataSource);
        medicalHistoryService = new MedicalHistoryService(medicalHistoryDAO);

        insertTestUser();
        controller.setMedicalHistoryService(medicalHistoryService);

        SceneManager.init(stage);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @AfterEach public void cleanup() {
        ExceptionHandler.setLoggingEnabled(false);
        SessionManager.getInstance().clearSession();

        try (java.sql.Connection conn = h2DataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM MedicalHistory WHERE UserID=" + testUserId);
            stmt.execute("DELETE FROM Users WHERE UserID=" + testUserId);
        } catch (java.sql.SQLException e) {
        }
    }

    private void fillForm(boolean conditions, boolean medications, boolean vaccination, boolean allergies) {
        interact(() -> {
            lookup("#longTermConditions" + (conditions ? "Yes" : "No")).queryAs(RadioButton.class).setSelected(true);
            lookup("#longTermMedications" + (medications ? "Yes" : "No")).queryAs(RadioButton.class).setSelected(true);
            lookup("#upToDateVaccinations" + (vaccination ? "Yes" : "No")).queryAs(RadioButton.class).setSelected(true);
            lookup("#allergies" + (allergies ? "Yes" : "No")).queryAs(RadioButton.class).setSelected(true);
        });

        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test public void updateHistorySuccessIntegrationTest() throws TimeoutException {
        fillForm(true, true, true, true);
        clickOn("#submitUpdateMedicalHistoryButton");

        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
            MedicalHistory history = medicalHistoryDAO.getMedicalHistoryByUserId(testUserId);
            return history != null;
        });

        MedicalHistory savedHistory = medicalHistoryDAO.getMedicalHistoryByUserId(testUserId);
        assertNotNull(savedHistory, "Medical history record must be persisted.");
        assertEquals(testUserId, savedHistory.getUserID(), "Record must be linked to the logged-in user.");
        assertTrue(savedHistory.isLongTermConditions(), "LongTermConditions must be true.");
        assertTrue(savedHistory.isLongTermMedications(), "LongTermMedications must be true.");
        assertTrue(savedHistory.isVaccinationUpToDate(), "VaccinationsUpToDate must be true.");
        assertTrue(savedHistory.isAllergies(), "Allergies must be true.");
    }

    @Test public void updateHistoryMissingFieldValidationTest() {
        int initialCount = 0;

        interact(() -> {
            lookup("#longTermConditionsYes").queryAs(RadioButton.class).setSelected(true);
            lookup("#longTermMedicationsYes").queryAs(RadioButton.class).setSelected(true);
            lookup("#upToDateVaccinationsYes").queryAs(RadioButton.class).setSelected(true);
        });

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#submitUpdateMedicalHistoryButton");

        WaitForAsyncUtils.waitForFxEvents();

        MedicalHistory savedHistory = medicalHistoryDAO.getMedicalHistoryByUserId(testUserId);
        assertNull(savedHistory, "No medical history record should have been persisted due to missing field.");
    }
}