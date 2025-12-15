package com.example.infection_monitoring_system_desktop_application.Integration;

import com.example.infection_monitoring_system_desktop_application.Controller.EditDetailsController;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Model.*;
import org.testfx.framework.junit5.ApplicationTest;
import java.util.concurrent.TimeoutException;
import org.testfx.util.WaitForAsyncUtils;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
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

import static org.junit.jupiter.api.Assertions.*;

class EditDetailsControllerTest extends ApplicationTest {

    private EditDetailsController controller;
    private UserService userService;
    private UserDAO userDAO;
    private DataSource h2DataSource;

    private String originalEmail = "edit@test.com";
    private String updatedEmail = "new@test.com";

    private void createSQLSchema() {
        try (var conn = h2DataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS Users");
            stmt.execute("""
                CREATE TABLE Users (
                    UserID INT AUTO_INCREMENT PRIMARY KEY,
                    Email VARCHAR(255) UNIQUE,
                    Password VARCHAR(255),
                    FirstName VARCHAR(255),
                    LastName VARCHAR(255),
                    DateOfBirth DATE,
                    AddressLine1 VARCHAR(255),
                    AddressLine2 VARCHAR(255),
                    TownCity VARCHAR(255),
                    County VARCHAR(255),
                    Postcode VARCHAR(50),
                    AccountStatus VARCHAR(50),
                    Role VARCHAR(50)
                )
            """);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }

    private void insertTestUser() {
        User user = new GeneralPublicUser(
                originalEmail,
                PasswordUtils.hashPassword("password"),
                "Old",
                "Name",
                LocalDate.of(1990, 1, 1),
                "Addr1", "Addr2", "City", "County", "POST",
                User.AccountStatus.Active
        );

        userDAO.addUser(user);

        User savedUser = userDAO.getUserByEmail(originalEmail);
        if (savedUser == null) {
            throw new RuntimeException("Failed to insert test user.");
        }

        SessionManager.getInstance().setCurrentUser(savedUser);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/example/infection_monitoring_system_desktop_application/View/EditDetails.fxml"
                ),
                bundle
        );

        Parent root = loader.load();
        controller = loader.getController();

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb_edit;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        this.h2DataSource = ds;

        createSQLSchema();

        userDAO = new UserDAO(h2DataSource);
        userService = new UserService(userDAO);
        controller.setUserService(userService);

        insertTestUser();

        SceneManager.init(stage);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void editDetailsSuccessfullyUpdatesUser() throws TimeoutException {
        interact(() -> {
            lookup("#fieldFirstName").queryAs(TextField.class).setText("New");
            lookup("#fieldLastName").queryAs(TextField.class).setText("User");
            lookup("#fieldEmail").queryAs(TextField.class).setText(updatedEmail);
            lookup("#fieldDob").queryAs(DatePicker.class).setValue(LocalDate.of(1995, 5, 5));
            lookup("#fieldAddress1").queryAs(TextField.class).setText("NewAddr");
            lookup("#fieldCity").queryAs(TextField.class).setText("NewCity");
            lookup("#fieldCounty").queryAs(TextField.class).setText("NewCounty");
            lookup("#fieldPostcode").queryAs(TextField.class).setText("NEW123");
        });

        clickOn("#submitEditDetailsButton");

        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () ->
                userDAO.getUserByEmail(updatedEmail) != null
        );

        User updated = userDAO.getUserByEmail(updatedEmail);

        assertNotNull(updated);
        assertEquals("New", updated.getFirstName());
        assertEquals("User", updated.getLastName());
        assertEquals(updatedEmail, updated.getEmail());
        assertEquals("NewCity", updated.getTownCity());

        assertNull(userDAO.getUserByEmail(originalEmail),
                "Original email should no longer exist after update.");
    }

    @AfterEach
    void cleanup() {
        SessionManager.getInstance().clearSession();
    }
}
