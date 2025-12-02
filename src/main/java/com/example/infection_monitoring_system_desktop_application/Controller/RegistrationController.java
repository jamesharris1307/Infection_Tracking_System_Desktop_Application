package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class RegistrationController {

    @FXML private ComboBox<String> roleComboBox;

    @FXML private VBox page1;
    @FXML private VBox page2;
    @FXML private VBox page3;

    @FXML private Button next1Button;
    @FXML private Button next2Button;
    @FXML private Button back2Button;
    @FXML private Button back3Button;
    @FXML private Button submitButton;

    @FXML private Circle step1Circle;
    @FXML private Circle step2Circle;
    @FXML private Circle step3Circle;

    @FXML private Label step1Label;
    @FXML private Label step2Label;
    @FXML private Label step3Label;

    private int currentPage = 1;

    @FXML
    private void initialize() {
        showPage(currentPage);

        next1Button.setOnAction(e -> goToPage(2));
        back2Button.setOnAction(e -> goToPage(1));
        next2Button.setOnAction(e -> goToPage(3));
        back3Button.setOnAction(e -> goToPage(2));
        submitButton.setOnAction(e -> handleSubmit());
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
        System.out.println("Form submitted!");
    }

    @FXML
    private void viewLogin() {
        SceneManager.switchScene("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
    }
}
