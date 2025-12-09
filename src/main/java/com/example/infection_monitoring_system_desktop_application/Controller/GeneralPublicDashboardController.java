package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class GeneralPublicDashboardController {

    @FXML private StackPane rootScaler;
    @FXML private AnchorPane rootContent;

    @FXML private VBox pageHome;
    @FXML private ScrollPane submitReportPage;
    @FXML private ScrollPane updateMedicalHistoryPage;
    @FXML private ScrollPane editDetailsPage;
    @FXML private VBox pageHome1;

    @FXML private Button logoutBtn;
    @FXML private Button settingsBtn;

    @FXML private Button updateMedicalHistoryButton;
    @FXML private Button submitReportButton;
    @FXML private Button viewSubmissionsButton;

    @FXML private Button backButton;
    @FXML private DatePicker symptomStartDate;
    @FXML private Button submitReportDetailsButton;

    @FXML private Button btnBackUpdateHistory;
    @FXML private Button submitUpdateMedicalHistoryButton;

    @FXML private Button btnBackEditDetails;
    @FXML private TextField fieldFirstName;
    @FXML private TextField fieldLastName;
    @FXML private TextField fieldEmail;
    @FXML private DatePicker fieldDob;
    @FXML private TextField fieldAddress1;
    @FXML private TextField fieldAddress2;
    @FXML private TextField fieldCity;
    @FXML private TextField fieldCounty;
    @FXML private TextField fieldPostcode;
    @FXML private TextField fieldRole;
    @FXML private TextField fieldPassword;
    @FXML private TextField fieldConfirmPassword;
    @FXML private Button btnSubmitEditDetails;
    @FXML private Button btnDeleteAccount;

    @FXML private Button btnUpdateHistory1;
    @FXML private Button btnSubmitReport1;
    @FXML private Button btnViewSubmissions1;

    @FXML private Label lblUserName;
    @FXML private Label lblCurrentDate;
    @FXML private Label lblHistoryDate;
    @FXML private Label lblVaccStatus;
    @FXML private Label lblExistingConditions;


    @FXML
    private void initialize() {
        updateMedicalHistoryButton.setOnAction(e -> showUpdateHistory());
        submitReportButton.setOnAction(e -> showSubmitReport());
        btnDeleteAccount.setOnAction(e -> handleDeleteAccount());
        //viewSubmissionsButton.setOnAction(e -> showEditDetails());

        backButton.setOnAction(e -> showHome());
        btnBackUpdateHistory.setOnAction(e -> showHome());
        btnBackEditDetails.setOnAction(e -> showHome());

        settingsBtn.setOnAction(e -> openSettingsPopup());
        btnUpdateHistory1.setOnAction(e -> handleChangeTheme());
        btnSubmitReport1.setOnAction(e -> handleChangeLanguage());
        btnViewSubmissions1.setOnAction(e -> handleEditProfileFromSettings());

        logoutBtn.setOnAction(e -> handleLogout());

        showHome();

        pageHome1.setVisible(false);
        pageHome1.setMouseTransparent(true);
    }

    private void hideAllPages() {
        pageHome.setVisible(false);
        submitReportPage.setVisible(false);
        updateMedicalHistoryPage.setVisible(false);
        editDetailsPage.setVisible(false);
    }

    private void showHome() {
        hideAllPages();
        pageHome.setVisible(true);
        closeSettingsPopup();
    }

    private void showSubmitReport() {
        hideAllPages();
        submitReportPage.setVisible(true);
        closeSettingsPopup();
    }

    private void showUpdateHistory() {
        hideAllPages();
        updateMedicalHistoryPage.setVisible(true);
        closeSettingsPopup();
    }

    private void showEditDetails() {
        hideAllPages();
        editDetailsPage.setVisible(true);
        closeSettingsPopup();
    }

    private void openSettingsPopup() {
        pageHome1.setVisible(true);
        pageHome1.setMouseTransparent(false);
    }

    private void closeSettingsPopup() {
        pageHome1.setVisible(false);
        pageHome1.setMouseTransparent(true);
    }

    @FXML
    private void handleChangeTheme() {
        ThemeManager.getInstance().toggleTheme();
        SceneManager.refreshCurrentScene();
        closeSettingsPopup();
    }

    @FXML
    private void handleChangeLanguage() {
        LanguageManager.toggleLanguage();
        SceneManager.refreshCurrentScene();
        closeSettingsPopup();
    }

    private void handleEditProfileFromSettings() {
        closeSettingsPopup();
        showEditDetails();
    }

    @FXML
    private void handleSubmitReport() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateMedicalHistory() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditDetailsSubmit() {
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

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || dob == null ||
                    address1.isEmpty() || address2.isEmpty() || city.isEmpty() || county.isEmpty() || postcode.isEmpty()) {
                AlertUtils.showError("Error", "Please fill in all required fields.");
                return;
            }

            String hashedPassword = password.isEmpty() ? null : PasswordUtils.hashPassword(password);

            User currentUser = SessionManager.getInstance().getCurrentUser();
            String originalEmail = currentUser.getEmail();

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

            UserDAO userDAO = new UserDAO();
            userDAO.updateUser(currentUser, originalEmail);

            AlertUtils.showInfo("Success", "Profile updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Error", "An error occurred while updating your profile.");
        }
    }

    @FXML
    private void handleDeleteAccount() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        String email = currentUser.getEmail();

        UserDAO userDAO = new UserDAO();
        userDAO.deleteUser(email);

        SessionManager.getInstance().clearSession();
        SceneManager.switchScene("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");

        AlertUtils.showInfo("Account Deleted", "Your account has been deleted successfully.");
    }


    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        SceneManager.switchScene("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
    }
}
