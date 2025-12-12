package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.Case;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class HealthcareProfessionalController {

    @FXML private TableView<Case> casesTable;
    @FXML private TableColumn<Case, Integer> colCaseId;
    @FXML private TableColumn<Case, Integer> colUserId;
    @FXML private TableColumn<Case, String> colFirstName;
    @FXML private TableColumn<Case, String> colLastName;
    @FXML private TableColumn<Case, LocalDate> colDateReported;
    @FXML private TableColumn<Case, String> colSymptoms;
    @FXML private TableColumn<Case, LocalDate> colSymptomsBegan;
    @FXML private TableColumn<Case, String> colSeverity;
    @FXML private TableColumn<Case, Boolean> colExposure;

    @FXML private Button btnFilter;
    @FXML private Button btnSearch;
    @FXML private Button btnSort;

    private final CaseDAO caseDAO = new CaseDAO();
    private List<Case> allCases;

    @FXML
    public void initialize() {

        colCaseId.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getCaseID())
        );

        colUserId.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getUser() != null
                        ? c.getValue().getUser().getUserId()
                        : null)
        );

        colFirstName.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getUser() != null ? c.getValue().getUser().getFirstName() : ""
                )
        );

        colLastName.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getUser() != null ? c.getValue().getUser().getLastName() : ""
                )
        );

        colDateReported.setCellValueFactory(c -> {
            LocalDateTime dt = c.getValue().getDateReported();
            return new SimpleObjectProperty<>(dt != null ? dt.toLocalDate() : null);
        });

        colSymptoms.setCellValueFactory(c ->
                new SimpleStringProperty(
                        String.join(", ", c.getValue().getSymptoms())
                )
        );

        colSymptomsBegan.setCellValueFactory(c -> {
            LocalDateTime dt = c.getValue().getSymptomsBegan();
            return new SimpleObjectProperty<>(dt != null ? dt.toLocalDate() : null);
        });

        colSeverity.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSeverity())
        );

        colExposure.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().isConfirmedExposure())
        );

        loadCases();
    }

    private void loadCases() {
        allCases = caseDAO.getAllCases();
        casesTable.setItems(FXCollections.observableArrayList(allCases));
    }
}
