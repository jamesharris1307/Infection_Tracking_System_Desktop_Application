package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
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
    @FXML private Button submitEditDetailsButton;
    @FXML private Button deleteAccountButton;
    @FXML private Button backButton;

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
            ExceptionHandler.handle(ExceptionFactory.validationError("Passwords do not match."),
                    "Profile update: Password mismatch");
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                dob == null || address1.isEmpty() || city.isEmpty() ||
                county.isEmpty() || postcode.isEmpty()) {
            ExceptionHandler.handle(ExceptionFactory.validationError("Please fill in all required fields."),
                    "Profile update: Missing required fields");
            return;
        }

        if (userService == null) {
            ExceptionHandler.handle(ExceptionFactory.unexpected(
                            new IllegalStateException("UserService dependency not initialized.")),
                    "CRITICAL: UserService is missing in EditDetailsController."
            );
            return;
        }

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            ExceptionHandler.handle(ExceptionFactory.userNotFound("N/A"), "CRITICAL: No user in session.");
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
                    // SUCCESS BLOCK: Show confirmation message
                    AlertUtils.showInfo("Success", "Profile updated successfully.");
                    clearForm();
                    if (parentController != null) {
                        parentController.showHomePage();
                    }
                }))
                .exceptionally(ex -> {
                    // FAILURE BLOCK: Handle exception centrally
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Profile update failed asynchronously.");
                    return null;
                });
    }

    @FXML private void handleDeleteAccount() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            ExceptionHandler.handle(ExceptionFactory.userNotFound("N/A"), "CRITICAL: No user in session.");
            return;
        }

        if (userService == null) {
            ExceptionHandler.handle(ExceptionFactory.unexpected(
                            new IllegalStateException("UserService dependency not initialized.")),
                    "CRITICAL: UserService is missing in EditDetailsController."
            );
            return;
        }

        userService.deleteUserAsync(currentUser)
                .thenRun(() -> javafx.application.Platform.runLater(() -> {
                    SessionManager.getInstance().clearSession();
                    SceneManager.switchRoot("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
                    AlertUtils.showInfo("Account Deleted", "Your account has been deleted.");
                }))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Account deletion failed asynchronously.");
                    return null;
                });
    }

    @FXML private void handleBack() {
        if (parentController != null) {
            parentController.showHomePage();
        } else {
            SceneManager.switchRoot("/com/example/infection_monitoring_system_desktop_application/View/GeneralPublicDashboard.fxml");
        }
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