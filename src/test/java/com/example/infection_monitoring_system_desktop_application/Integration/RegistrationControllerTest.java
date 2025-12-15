package com.example.infection_monitoring_system_desktop_application.Integration;

import com.example.infection_monitoring_system_desktop_application.Controller.RegistrationController;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Model.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.scene.control.PasswordField;
import org.testfx.util.WaitForAsyncUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import java.util.concurrent.TimeUnit;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javax.sql.DataSource;
import javafx.scene.Parent;
import java.time.LocalDate;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Statement;

public class RegistrationControllerTest extends ApplicationTest {

    private RegistrationController controller;
    private UserService userService;
    private UserDAO userDAO;
    private DataSource h2DataSource;

    private void createSQLSchema() {
        try (java.sql.Connection conn = h2DataSource.getConnection();
             Statement stmt = conn.createStatement()) {
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
            throw new RuntimeException("Failed to initialize USERS table schema in H2.", e);
        }
    }

    @Override public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/Registration.fxml"),
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

    @BeforeEach public void setup() {
        ExceptionHandler.setLoggingEnabled(false);
        userDAO.getAllUsers().forEach(u -> userDAO.deleteUser(u.getEmail()));
        SessionManager.getInstance().setCurrentUser(null);
    }

    @AfterEach public void cleanup() {
        ExceptionHandler.setLoggingEnabled(false);
    }

    private void fillForm(String email, String password, String firstName, String lastName, String role) {
        interact(() -> {
            lookup("#emailField").queryAs(TextField.class).setText(email);
            lookup("#passwordField").queryAs(PasswordField.class).setText(password);
            lookup("#confirmPasswordField").queryAs(PasswordField.class).setText(password);
            lookup("#firstNameField").queryAs(TextField.class).setText(firstName);
            lookup("#lastNameField").queryAs(TextField.class).setText(lastName);
            lookup("#dobPicker").queryAs(DatePicker.class).setValue(LocalDate.of(2000, 1, 1));
            lookup("#address1Field").queryAs(TextField.class).setText("123 Main St");
            lookup("#address2Field").queryAs(TextField.class).setText("Apt 4B");
            lookup("#cityField").queryAs(TextField.class).setText("Cardiff");
            lookup("#countyField").queryAs(TextField.class).setText("Glamorgan");
            lookup("#postcodeField").queryAs(TextField.class).setText("CF10 1AA");

            ComboBox<String> comboBox = lookup("#roleComboBox").queryAs(ComboBox.class);
            comboBox.getSelectionModel().select(role);
        });

        clickOn("#next1Button");
        clickOn("#next2Button");
    }

    private String uniqueEmail() {
        return "user_" + System.currentTimeMillis() + "@example.com";
    }

    private <T extends User> void assertUserPersisted(Class<T> clazz, String email) throws Exception {
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> userDAO.getUserByEmail(email) != null);
        User saved = userDAO.getUserByEmail(email);
        assertNotNull(saved);
        assertEquals(email, saved.getEmail());

        if (clazz.equals(GeneralPublicUser.class)) {
            assertEquals(User.AccountStatus.Active, saved.getAccountStatus(), "General Public should be Active on registration.");
        } else {
            assertEquals(User.AccountStatus.Disabled, saved.getAccountStatus(), "Professional users should be Disabled on registration.");
        }
    }

    @Test public void registerGeneralPublicIntegrationTest() throws Exception {
        String email = uniqueEmail();
        fillForm(email, "Password123", "John", "Doe", "General Public");
        clickOn("#submitButton");
        assertUserPersisted(GeneralPublicUser.class, email);
    }

    @Test public void registerHealthcareProfessionalIntegrationTest() throws Exception {
        String email = uniqueEmail();
        fillForm(email, "Password123", "Alice", "Smith", "Healthcare Professional");
        clickOn("#submitButton");
        assertUserPersisted(HealthcareProfessionalUser.class, email);
    }

    @Test public void registerAdministratorIntegrationTest() throws Exception {
        String email = uniqueEmail();
        fillForm(email, "Password123", "Bob", "Brown", "Administrator");
        clickOn("#submitButton");
        assertUserPersisted(AdministratorUser.class, email);
    }
}