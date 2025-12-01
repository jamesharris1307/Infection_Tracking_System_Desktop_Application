package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

        // Page navigation
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
        Color active = Color.web("#96bcb4");
        Color inactive = Color.web("#f1f9ff");
        Color textActive = Color.WHITE;
        Color textInactive = Color.web("#a1a1a1");

        step1Circle.setFill(page >= 1 ? active : inactive);
        step2Circle.setFill(page >= 2 ? active : inactive);
        step3Circle.setFill(page >= 3 ? active : inactive);

        step1Label.setTextFill(page >= 1 ? textActive : textInactive);
        step2Label.setTextFill(page >= 2 ? textActive : textInactive);
        step3Label.setTextFill(page >= 3 ? textActive : textInactive);
    }

    private void handleSubmit() {
        System.out.println("Form submitted!");
    }

    @FXML
    private void viewLogin() {
        SceneManager.switchScene("/com/example/infection_monitoring_system_desktop_application/View/Login.fxml");
    }
}
