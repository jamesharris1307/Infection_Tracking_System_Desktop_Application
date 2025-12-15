package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import javafx.fxml.FXML;

public class EditDetailsController {

    @FXML private TextField fieldFirstName;
    @FXML private TextField fieldLastName;
    @FXML private TextField fieldEmail;
    @FXML private DatePicker fieldDob;
    @FXML private TextField fieldAddress1;
    @FXML private TextField fieldAddress2;
    @FXML private TextField fieldCity;
    @FXML private TextField fieldCounty;
    @FXML private TextField fieldPostcode;
    @FXML private TextField fieldPassword;
    @FXML private TextField fieldConfirmPassword;

    private GeneralPublicDashboardController parentController;
    private UserService userService;

    public void setParentController(GeneralPublicDashboardController parent) {
        this.parentController = parent;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML private void handleEditDetailsSubmit() {
        String firstName = fieldFirstName.getText().trim();
        String lastName = fieldLastName.getText().trim();
        String email = fieldEmail.getText().trim();
        LocalDate dob = fieldDob.getValue();
        String address1 = fieldAddress1.getText().trim();
        String address2 = fieldAddress2.getText().trim();
        String city = fieldCity.getText().trim();
        String county = fieldCounty.getText().trim();
        String postcode = fieldPostcode.getText().trim();
        String password = fieldPassword.getText();
        String confirmPassword = fieldConfirmPassword.getText();

        if (!password.equals(confirmPassword)) {
            ExceptionHandler.handle(ExceptionFactory.validationError("Passwords Don't Match"),
                    "Profile Update: Password Mismatch");
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                dob == null || address1.isEmpty() || city.isEmpty() ||
                county.isEmpty() || postcode.isEmpty()) {
            ExceptionHandler.handle(ExceptionFactory.validationError("Fill in all Required Fields."),
                    "Profile Update: Missing required fields");
            return;
        }

        if (userService == null) {
            ExceptionHandler.handle(ExceptionFactory.unexpected(
                            new IllegalStateException("UserService Not Initialised")),
                    "CRITICAL: UserService Missing From EditDetailsController."
            );
            return;
        }

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            ExceptionHandler.handle(ExceptionFactory.userNotFound("N/A"), "CRITICAL: No User Found in Session");
            return;
        }

        String originalEmail = currentUser.getEmail();
        String hashedPassword = password.isEmpty() ? null : PasswordUtils.hashPassword(password);

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
        currentUser.setDateOfBirth(dob);
        currentUser.setAddressLine1(address1);
        currentUser.setAddressLine2(address2);
        currentUser.setTownCity(city);
        currentUser.setCounty(county);
        currentUser.setPostcode(postcode);

        if (hashedPassword != null) {
            currentUser.setPassword(hashedPassword);
        }

        userService.updateUserAsync(currentUser, originalEmail)
                .thenRun(() -> javafx.application.Platform.runLater(() -> {
                    AlertUtils.showInfo("Success", "Profile Successfully Updated");
                    clearForm();
                    if (parentController != null) {
                        parentController.showHomePage();
                    }
                }))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Profile Update Failed Asynchronously.");
                    return null;
                });
    }

    @FXML private void handleDeleteAccount() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            ExceptionHandler.handle(ExceptionFactory.userNotFound("N/A"), "CRITICAL: No User Found in Session");
            return;
        }

        if (userService == null) {
            ExceptionHandler.handle(ExceptionFactory.unexpected(
                            new IllegalStateException("UserService Not Initialized.")),
                    "CRITICAL: UserService Missing From EditDetailsController."
            );
            return;
        }

        userService.deleteUserAsync(currentUser)
                .thenRun(() -> javafx.application.Platform.runLater(() -> {
                    SessionManager.getInstance().clearSession();
                    SceneManager.switchRoot("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
                    AlertUtils.showInfo("Account Deleted", "Account Successfully Deleted.");
                }))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Delete Account Failed Asynchronously.");
                    return null;
                });
    }

    private void clearForm() {
        fieldFirstName.clear();
        fieldLastName.clear();
        fieldEmail.clear();
        fieldDob.setValue(null);
        fieldAddress1.clear();
        fieldAddress2.clear();
        fieldCity.clear();
        fieldCounty.clear();
        fieldPostcode.clear();
        fieldPassword.clear();
        fieldConfirmPassword.clear();
    }
}