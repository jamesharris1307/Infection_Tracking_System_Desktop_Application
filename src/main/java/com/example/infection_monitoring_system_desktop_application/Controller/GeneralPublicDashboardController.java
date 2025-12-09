package com.example.infection_monitoring_system_desktop_application.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class GeneralPublicDashboardController {

    @FXML private StackPane rootScaler;
    @FXML private AnchorPane rootContent;

    @FXML private VBox pageHome;
    @FXML private ScrollPane submitReportPage;
    @FXML private ScrollPane updateMedicalHistoryPage;
    @FXML private ScrollPane editDetailsPage;
    @FXML private VBox pageHome1; // Settings popup panel

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

    @FXML private Button btnUpdateHistory1;   // Change Theme
    @FXML private Button btnSubmitReport1;    // Change Language
    @FXML private Button btnViewSubmissions1; // Edit Profile

    @FXML private Label lblUserName;
    @FXML private Label lblCurrentDate;
    @FXML private Label lblHistoryDate;
    @FXML private Label lblVaccStatus;
    @FXML private Label lblExistingConditions;


    @FXML
    private void initialize() {
        // HOME PAGE NAV
        updateMedicalHistoryButton.setOnAction(e -> showUpdateHistory());
        submitReportButton.setOnAction(e -> showSubmitReport());
        viewSubmissionsButton.setOnAction(e -> showEditDetails());

        // BACK BUTTONS
        backButton.setOnAction(e -> showHome());
        btnBackUpdateHistory.setOnAction(e -> showHome());
        btnBackEditDetails.setOnAction(e -> showHome());

        // SETTINGS
        settingsBtn.setOnAction(e -> openSettingsPopup());
        btnUpdateHistory1.setOnAction(e -> handleChangeTheme());
        btnSubmitReport1.setOnAction(e -> handleChangeLanguage());
        btnViewSubmissions1.setOnAction(e -> handleEditProfileFromSettings());

        // Logout
        logoutBtn.setOnAction(e -> handleLogout());

        // Load home page initially
        showHome();

        pageHome1.setVisible(false);
        pageHome1.setMouseTransparent(true);
    }

    /* -------------------------
       NAVIGATION LOGIC
       ------------------------- */

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

    /* -------------------------
       SETTINGS POPUP
       ------------------------- */

    private void openSettingsPopup() {
        pageHome1.setVisible(true);
        pageHome1.setMouseTransparent(false);
    }

    private void closeSettingsPopup() {
        pageHome1.setVisible(false);
        pageHome1.setMouseTransparent(true);
    }

    /* -------------------------
       SETTINGS ACTIONS
       ------------------------- */

    private void handleChangeTheme() {
        // Attach your theme logic here
        closeSettingsPopup();
    }

    private void handleChangeLanguage() {
        // Attach your language switching logic here
        closeSettingsPopup();
    }

    private void handleEditProfileFromSettings() {
        closeSettingsPopup();
        showEditDetails();
    }

    /* -------------------------
       FORM SUBMISSION STUBS
       ------------------------- */

    @FXML
    private void handleSubmitReport() {
        // Validate and submit
    }

    @FXML
    private void handleUpdateMedicalHistory() {
        // Validate and submit
    }

    @FXML
    private void handleEditDetailsSubmit() {
        // Validate and submit
    }

    /* -------------------------
       LOGOUT
       ------------------------- */

    private void handleLogout() {
        // Switch scene to login page
    }
}
