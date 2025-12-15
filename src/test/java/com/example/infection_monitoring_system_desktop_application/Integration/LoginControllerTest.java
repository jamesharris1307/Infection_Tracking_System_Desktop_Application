package com.example.infection_monitoring_system_desktop_application.Integration;

import com.example.infection_monitoring_system_desktop_application.Controller.LoginController;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Model.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.util.concurrent.TimeUnit;
import org.h2.jdbcx.JdbcDataSource;
import java.util.ResourceBundle;
import org.junit.jupiter.api.*;
import javafx.fxml.FXMLLoader;
import javax.sql.DataSource;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.time.LocalDate;

public class LoginControllerTest extends ApplicationTest {

    private UserService userService;
    private UserDAO userDAO;
    private LoginController controller;
    private DataSource h2DataSource;

    private void createSQLSchema() {
        try (java.sql.Connection conn = h2DataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS Users");

            String createTableSQL = """
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
        """;
            stmt.execute(createTableSQL);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Failed to create USERS table schema in H2.", e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml"),
                bundle
        );
        Parent root = loader.load();

        controller = loader.getController();

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        this.h2DataSource = ds;

        createSQLSchema();

        userDAO = new UserDAO(this.h2DataSource);
        userService = new UserService(userDAO);
        controller.setUserService(userService);

        SceneManager.init(stage);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setup() {
        ExceptionHandler.setLoggingEnabled(false);
        userDAO.getAllUsers().forEach(u -> userDAO.deleteUser(u.getEmail()));
        SessionManager.getInstance().setCurrentUser(null);
    }

    @AfterEach
    public void cleanup() {
        ExceptionHandler.setLoggingEnabled(false);
    }

    private void seedUser(User user) {
        userDAO.addUser(user);
    }

    @Test
    public void loginAsGeneralPublicIntegrationTest() throws Exception {
        User user = new GeneralPublicUser(
                "user@example.com",
                PasswordUtils.hashPassword("password123"),
                "First", "Last",
                LocalDate.of(2000, 1, 1),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        );
        seedUser(user);

        clickOn("#usernameTextField").write("user@example.com");
        clickOn("#passwordField").write("password123");
        clickOn("#loginButton");

        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () ->
                SessionManager.getInstance().getCurrentUser() != null
        );

        assertEquals("user@example.com", SessionManager.getInstance().getCurrentUser().getEmail());
    }

    @Test
    public void loginAsHealthcareProfessionalIntegrationTest() throws Exception {
        User user = new HealthcareProfessionalUser(
                "user@example.com",
                PasswordUtils.hashPassword("password123"),
                "First", "Last",
                LocalDate.of(2000, 1, 1),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        );
        seedUser(user);

        clickOn("#usernameTextField").write("user@example.com");
        clickOn("#passwordField").write("password123");
        clickOn("#loginButton");

        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () ->
                SessionManager.getInstance().getCurrentUser() != null
        );

        assertEquals("user@example.com", SessionManager.getInstance().getCurrentUser().getEmail());
    }

    @Test
    public void loginAsAdministratorIntegrationTest() throws Exception {
        User user = new AdministratorUser(
                "user@example.com",
                PasswordUtils.hashPassword("password123"),
                "First", "Last",
                LocalDate.of(2000, 1, 1),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        );
        seedUser(user);

        clickOn("#usernameTextField").write("user@example.com");
        clickOn("#passwordField").write("password123");
        clickOn("#loginButton");

        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () ->
                SessionManager.getInstance().getCurrentUser() != null
        );

        assertEquals("user@example.com", SessionManager.getInstance().getCurrentUser().getEmail());
    }

    @Test
    public void loginInvalidPasswordIntegrationTest() throws Exception {
        User user = new GeneralPublicUser(
                "user@example.com",
                PasswordUtils.hashPassword("correctpassword"),
                "First", "Last",
                LocalDate.of(2000, 1, 1),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        );
        seedUser(user);

        clickOn("#usernameTextField").write("user@example.com");
        clickOn("#passwordField").write("wrongpassword");
        clickOn("#loginButton");

        WaitForAsyncUtils.waitFor(3, TimeUnit.SECONDS, () -> true);

        assertNull(SessionManager.getInstance().getCurrentUser());
    }

    @Test
    public void loginUserNotFoundIntegrationTest() throws Exception {
        clickOn("#usernameTextField").write("missing@example.com");
        clickOn("#passwordField").write("password123");
        clickOn("#loginButton");

        WaitForAsyncUtils.waitFor(3, TimeUnit.SECONDS, () -> true);

        assertNull(SessionManager.getInstance().getCurrentUser());
    }
}