package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.GeneralPublicUser;
import com.example.infection_monitoring_system_desktop_application.Model.HealthcareProfessionalUser;
import com.example.infection_monitoring_system_desktop_application.Model.AdministratorUser;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Util.LanguageManager;
import com.example.infection_monitoring_system_desktop_application.Util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegistrationController {

    @FXML private VBox page1;
    @FXML private VBox page2;
    @FXML private VBox page3;

    @FXML private Circle step1Circle;
    @FXML private Circle step2Circle;
    @FXML private Circle step3Circle;
    @FXML private Label step1Label;
    @FXML private Label step2Label;
    @FXML private Label step3Label;

    @FXML private Button next1Button;
    @FXML private Button next2Button;
    @FXML private Button back2Button;
    @FXML private Button back3Button;
    @FXML private Button submitButton;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField address1Field;
    @FXML private TextField address2Field;
    @FXML private TextField cityField;
    @FXML private TextField countyField;
    @FXML private TextField postcodeField;
    @FXML private ComboBox<String> roleComboBox;

    private int currentPage = 1;

    @FXML
    private void initialize() {
        showPage(currentPage);
        populateRoleComboBox();

        next1Button.setOnAction(e -> goToPage(2));
        back2Button.setOnAction(e -> goToPage(1));
        next2Button.setOnAction(e -> goToPage(3));
        back3Button.setOnAction(e -> goToPage(2));
        submitButton.setOnAction(e -> handleSubmit());
    }

    private void populateRoleComboBox() {
        ResourceBundle bundle = LanguageManager.getBundle();
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll(
                bundle.getString("general-public"),
                bundle.getString("healthcare-professional"),
                bundle.getString("administrator")
        );
    }

    private void goToPage(int pageNumber) {
        currentPage = pageNumber;
        showPage(currentPage);
    }

    private void showPage(int pageNumber) {
        page1.setVisible(pageNumber == 1);
        page2.setVisible(pageNumber == 2);
        page3.setVisible(pageNumber == 3);
        updateStepIndicator(pageNumber);
    }

    private void updateStepIndicator(int page) {
        updateStep(step1Circle, step1Label, page == 1);
        updateStep(step2Circle, step2Label, page == 2);
        updateStep(step3Circle, step3Label, page == 3);
    }

    private void updateStep(Circle circle, Label label, boolean active) {
        circle.getStyleClass().removeAll("active-page-indicator", "inactive-page-indicator");
        label.getStyleClass().removeAll("active-page-indicator-label", "inactive-page-indicator-label");
        if (active) {
            circle.getStyleClass().add("active-page-indicator");
            label.getStyleClass().add("active-page-indicator-label");
        } else {
            circle.getStyleClass().add("inactive-page-indicator");
            label.getStyleClass().add("inactive-page-indicator-label");
        }
    }

    private void handleSubmit() {
        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            LocalDate dob = dobPicker.getValue();
            String address1 = address1Field.getText().trim();
            String address2 = address2Field.getText().trim();
            String city = cityField.getText().trim();
            String county = countyField.getText().trim();
            String postcode = postcodeField.getText().trim();
            String roleSelection = roleComboBox.getValue();

            if (roleSelection != null) {
                switch (roleSelection) {
                    case "Y Cyhoedd Cyffredinol" -> roleSelection = "General Public";
                    case "Gweithiwr Gofal Iechyd" -> roleSelection = "Healthcare Professional";
                    case "Gweinyddwr" -> roleSelection = "Administrator";
                }
            }

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    !password.equals(confirmPassword) || firstName.isEmpty() || lastName.isEmpty() || roleSelection == null) {
                showAlert("Error", "Please fill in all required fields and ensure passwords match.");
                return;
            }

            User.Role roleEnum;
            User.AccountStatus accountStatusEnum;

            switch (roleSelection) {
                case "General Public" -> {
                    roleEnum = User.Role.GeneralPublic;
                    accountStatusEnum = User.AccountStatus.Active;
                }
                case "Healthcare Professional" -> {
                    roleEnum = User.Role.HealthcareProfessional;
                    accountStatusEnum = User.AccountStatus.Disabled;
                }
                case "Administrator" -> {
                    roleEnum = User.Role.Administrator;
                    accountStatusEnum = User.AccountStatus.Disabled;
                }
                default -> throw new IllegalArgumentException("Invalid role selected");
            }

            User newUser;

            switch (roleSelection) {
                case "General Public" -> newUser = new GeneralPublicUser(
                        email, password, firstName, lastName, dob,
                        address1, address2, city, county, postcode,
                        User.AccountStatus.Active
                );
                case "Healthcare Professional" -> newUser = new HealthcareProfessionalUser(
                        email, password, firstName, lastName, dob,
                        address1, address2, city, county, postcode,
                        User.AccountStatus.Disabled
                );
                case "Administrator" -> newUser = new AdministratorUser(
                        email, password, firstName, lastName, dob,
                        address1, address2, city, county, postcode,
                        User.AccountStatus.Disabled
                );
                default -> throw new IllegalArgumentException("Invalid role selected");
            }

            UserDAO userDAO = new UserDAO();
            userDAO.addUser(newUser);


            showAlert("Success", "User registered successfully!");
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while registering the user.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        firstNameField.clear();
        lastNameField.clear();
        dobPicker.setValue(null);
        address1Field.clear();
        address2Field.clear();
        cityField.clear();
        countyField.clear();
        postcodeField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        goToPage(1);
    }

    @FXML
    private void viewLogin() {
        SceneManager.switchScene("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
    }
}