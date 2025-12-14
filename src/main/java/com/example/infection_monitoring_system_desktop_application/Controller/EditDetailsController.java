package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
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
        try {
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
                AlertUtils.showError("Error", "Passwords do not match.");
                return;
            }

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    dob == null || address1.isEmpty() || city.isEmpty() ||
                    county.isEmpty() || postcode.isEmpty()) {
                AlertUtils.showError("Error", "Please fill in all required fields.");
                return;
            }

            if (userService == null) {
                AlertUtils.showError("System Error", "UserService dependency not initialized.");
                return;
            }

            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                AlertUtils.showError("Error", "No user is currently logged in.");
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

            if (hashedPassword != null) currentUser.setPassword(hashedPassword);

            userService.updateUserAsync(currentUser, originalEmail)
                    .thenRun(() -> javafx.application.Platform.runLater(() -> {
                        AlertUtils.showInfo("Success", "Profile updated successfully.");
                        clearForm();
                        if (parentController != null) {
                            parentController.showHomePage();
                        }
                    }))
                    .exceptionally(ex -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        javafx.application.Platform.runLater(() ->
                                AlertUtils.showError("Update Failed", "Error: " + cause.getMessage())
                        );
                        return null;
                    });
        } catch (Exception e) {
            AlertUtils.showError("Unexpected Error", "An unexpected error occurred while updating your profile.");
        }
    }

    @FXML private void handleDeleteAccount() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            AlertUtils.showError("Error", "No user is currently logged in.");
            return;
        }

        if (userService == null) {
            AlertUtils.showError("System Error", "UserService dependency not initialized.");
            return;
        }

        userService.deleteUserAsync(currentUser)
                .thenRun(() -> javafx.application.Platform.runLater(() -> {
                    SessionManager.getInstance().clearSession();
                    SceneManager.switchRoot("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
                    AlertUtils.showInfo("Account Deleted", "Your account has been deleted.");
                }))
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() ->
                            AlertUtils.showError("Unexpected Error", "Failed to delete account.")
                    );
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