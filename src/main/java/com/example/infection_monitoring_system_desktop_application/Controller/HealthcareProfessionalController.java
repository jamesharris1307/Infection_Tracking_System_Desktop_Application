package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.Case;
import com.example.infection_monitoring_system_desktop_application.Service.CaseService;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @FXML private ComboBox<String> cmbSort;
    @FXML private Button btnSort;

    private final CaseService caseService = new CaseService();
    private final ObservableList<Case> sortedCases = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            setupColumns();
            setupSortComboBox();
            loadCases();
        } catch (Exception e) {
            AlertUtils.showError("Unexpected Error", "Failed to initialise case table.");
        }
    }

    private void setupColumns() {
        colCaseId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCaseID()));
        colUserId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getUser() != null ? c.getValue().getUser().getUserId() : null));
        colFirstName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUser() != null ? c.getValue().getUser().getFirstName() : ""));
        colLastName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUser() != null ? c.getValue().getUser().getLastName() : ""));
        colDateReported.setCellValueFactory(c -> {
            LocalDateTime dt = c.getValue().getDateReported();
            return new SimpleObjectProperty<>(dt != null ? dt.toLocalDate() : null);
        });
        colSymptoms.setCellValueFactory(c -> new SimpleStringProperty(String.join(", ", c.getValue().getSymptoms())));
        colSymptomsBegan.setCellValueFactory(c -> {
            LocalDateTime dt = c.getValue().getSymptomsBegan();
            return new SimpleObjectProperty<>(dt != null ? dt.toLocalDate() : null);
        });
        colSeverity.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSeverity()));
        colExposure.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().isConfirmedExposure()));
    }

    private void setupSortComboBox() {
        cmbSort.getItems().addAll("Newest First", "Oldest First");
        cmbSort.getSelectionModel().selectFirst();
    }

    private void loadCases() {
        caseService.getAllCasesAsync()
                .thenAccept(cases -> Platform.runLater(() -> {
                    sortedCases.setAll(cases);
                    casesTable.setItems(sortedCases);
                }))
                .exceptionally(ex -> {
                    AlertUtils.showError("Error", "Failed to load cases.");
                    return null;
                });
    }

    @FXML
    private void onSortClicked() {
        String selection = cmbSort.getValue();

        if ("Oldest First".equals(selection)) {
            FXCollections.sort(sortedCases, (a, b) -> a.getDateReported().compareTo(b.getDateReported()));
        } else {
            FXCollections.sort(sortedCases, (a, b) -> b.getDateReported().compareTo(a.getDateReported()));
        }

        casesTable.setItems(sortedCases);
    }

}
