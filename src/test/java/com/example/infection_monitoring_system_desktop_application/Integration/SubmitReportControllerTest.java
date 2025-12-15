package com.example.infection_monitoring_system_desktop_application.Integration;

import com.example.infection_monitoring_system_desktop_application.Controller.SubmitReportController;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.CaseService;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Model.*;
import org.testfx.framework.junit5.ApplicationTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.TimeoutException;
import org.testfx.util.WaitForAsyncUtils;
import javafx.scene.control.RadioButton;
import javafx.scene.control.DatePicker;
import org.h2.jdbcx.JdbcDataSource;
import java.util.concurrent.TimeUnit;
import java.util.ResourceBundle;
import org.junit.jupiter.api.*;
import javafx.fxml.FXMLLoader;
import javax.sql.DataSource;
import javafx.scene.Parent;
import java.time.LocalDate;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Statement;

public class SubmitReportControllerTest extends ApplicationTest {

    private SubmitReportController controller;
    private CaseService caseService;
    private CaseDAO caseDAO;
    private UserDAO userDAO;
    private DataSource h2DataSource;
    private int testUserId;

    private void createSQLSchema() {
        try (java.sql.Connection conn = h2DataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS Cases");
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
                CREATE TABLE Cases (
                    CaseID INT AUTO_INCREMENT PRIMARY KEY,
                    UserID INT,
                    DateReported TIMESTAMP,
                    SymptomsBegan TIMESTAMP,
                    Symptoms VARCHAR(1024),
                    Severity VARCHAR(50),
                    ConfirmedExposure BOOLEAN,
                    FOREIGN KEY (UserID) REFERENCES USERS(UserID)
                )
            """);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }

    private void insertTestUser() {
        User user = new GeneralPublicUser(
                "report_test@example.com",
                PasswordUtils.hashPassword("password123"),
                "Report", "User",
                LocalDate.of(2000, 1, 1),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        );
        userDAO.addUser(user);

        User savedUser = userDAO.getUserByEmail(user.getEmail());
        if (savedUser != null) {
            testUserId = savedUser.getUserId();
            SessionManager.getInstance().setCurrentUser(savedUser);
        } else {
            throw new RuntimeException("Failed to insert and retrieve test user.");
        }
    }


    @Override public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/SubmitReport.fxml"),
                bundle
        );
        Parent root = loader.load();
        controller = loader.getController();

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb_report;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        this.h2DataSource = ds;
        createSQLSchema();

        userDAO = new UserDAO(this.h2DataSource);
        caseDAO = new CaseDAO(this.h2DataSource);
        caseService = new CaseService(caseDAO);

        insertTestUser();
        controller.setCaseService(caseService);

        SceneManager.init(stage);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @AfterEach public void cleanup() {
        ExceptionHandler.setLoggingEnabled(false);
        SessionManager.getInstance().clearSession();
    }

    private void fillAndSubmitForm(LocalDate startDate) throws TimeoutException {
        interact(() -> {
            lookup("#feverSymptomCheck").queryAs(RadioButton.class).setSelected(true);
            lookup("#ModerateSeverityCheck").queryAs(RadioButton.class).setSelected("Moderate".equals("Moderate"));
            lookup("#YesWorsenedSymptomCheck").queryAs(RadioButton.class).setSelected(true);
            lookup("#symptomsStartDate").queryAs(DatePicker.class).setValue(startDate);
        });

        clickOn("#submitReportButton");

        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> !caseDAO.getAllCases().isEmpty());
    }

    @Test public void submitReportSuccessIntegrationTest() throws TimeoutException {
        LocalDate symptomsBeganDate = LocalDate.now().minusDays(5);

        fillAndSubmitForm(symptomsBeganDate);

        assertEquals(1, caseDAO.getAllCases().size(), "A single case report should have been persisted.");

        Case savedCase = caseDAO.getAllCases().getFirst();

        assertEquals(testUserId, savedCase.getUserID(), "The case must be linked to the logged-in user ID.");
        assertTrue(savedCase.getSymptoms().contains("Fever"), "The 'Fever' symptom must be recorded.");
        assertEquals("Moderate", savedCase.getSeverity(), "The selected severity must be recorded.");
        assertEquals(symptomsBeganDate, savedCase.getSymptomsBegan().toLocalDate(), "The start date must match input.");
    }

    @Test public void submitReportMissingSeverityValidationTest() {
        int initialCaseCount = caseDAO.getAllCases().size();

        interact(() -> {
            lookup("#feverSymptomCheck").queryAs(RadioButton.class).setSelected(true);
            lookup("#symptomsStartDate").queryAs(DatePicker.class).setValue(LocalDate.now());
        });
        clickOn("#submitReportButton");

        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(initialCaseCount, caseDAO.getAllCases().size(), "No case report should have been persisted due to missing severity.");
    }
}