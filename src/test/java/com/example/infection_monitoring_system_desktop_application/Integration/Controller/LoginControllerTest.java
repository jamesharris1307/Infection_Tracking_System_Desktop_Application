package com.example.infection_monitoring_system_desktop_application.Integration.Controller;

import com.example.infection_monitoring_system_desktop_application.Controller.LoginController;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Model.*;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class LoginControllerTest extends ApplicationTest {

    private UserService userServiceMock;
    private LoginController controller;

    @Override public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml"),
                bundle
        );
        Parent root = loader.load();

        controller = loader.getController();
        userServiceMock = Mockito.mock(UserService.class);
        controller.setUserService(userServiceMock);

        SceneManager.init(stage);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach public void setup() {
        Mockito.reset(userServiceMock);
        ExceptionHandler.setLoggingEnabled(false);
    }

    @AfterEach public void cleanup() {
        ExceptionHandler.setLoggingEnabled(false);
    }

    @Test public void loginAsGeneralPublicTest() {
        testLoginWithRole(new GeneralPublicUser(
                "user@example.com",
                PasswordUtils.hashPassword("password123"),
                "First", "Last",
                LocalDate.of(2000, 1, 1),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        ), "user@example.com", "password123");
    }

    @Test public void loginAsHealthcareProfessionalTest() {
        testLoginWithRole(new HealthcareProfessionalUser(
                "health@example.com",
                PasswordUtils.hashPassword("password123"),
                "First", "Last",
                LocalDate.of(1990, 5, 20),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        ), "health@example.com", "password123");
    }

    @Test public void loginAsAdministratorTest() {
        testLoginWithRole(new AdministratorUser(
                "admin@example.com",
                PasswordUtils.hashPassword("admin123"),
                "Admin", "User",
                LocalDate.of(1985, 5, 20),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        ), "admin@example.com", "admin123");
    }

    private void testLoginWithRole(User user, String email, String password) {
        when(userServiceMock.getUserByEmailAsync(anyString()))
                .thenReturn(CompletableFuture.completedFuture(user));

        clickOn("#usernameTextField").write(email);
        clickOn("#passwordField").write(password);
        clickOn("#loginButton");

        verifyThat(".root", isVisible());
    }

    @Test public void loginUserNotFoundTest() {
        when(userServiceMock.getUserByEmailAsync(anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        clickOn("#usernameTextField").write("wrong@example.com");
        clickOn("#passwordField").write("wrongpassword");
        clickOn("#loginButton");

        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test public void loginInvalidPasswordTest() {
        User user = new GeneralPublicUser(
                "user@example.com",
                PasswordUtils.hashPassword("correctpassword"),
                "First", "Last",
                LocalDate.of(2000, 1, 1),
                "Addr1", "Addr2", "City", "County", "AB12 3CD",
                User.AccountStatus.Active
        );

        when(userServiceMock.getUserByEmailAsync(anyString()))
                .thenReturn(CompletableFuture.completedFuture(user));

        clickOn("#usernameTextField").write("user@example.com");
        clickOn("#passwordField").write("wrongpassword");
        clickOn("#loginButton");

        WaitForAsyncUtils.waitForFxEvents();
    }
}