package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.Case;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class HealthcareProfessionalController {

    @FXML private TableView<Case> casesTable;
    @FXML private TableColumn<Case, Integer> colCaseId;
    @FXML private TableColumn<Case, Integer> colUserId;
    @FXML private TableColumn<Case, String> colName;
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
        colCaseId.setCellValueFactory(new PropertyValueFactory<>("caseID"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userID"));
        colName.setCellValueFactory(cellData -> new SimpleStringProperty("User " + cellData.getValue().getUserID()));
        colDateReported.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getDateReported();
            return new SimpleObjectProperty<>(dt != null ? dt.toLocalDate() : null);
        });
        colSymptoms.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSymptoms() != null
                        ? String.join(", ", cellData.getValue().getSymptoms())
                        : ""
        ));
        colSymptomsBegan.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getSymptomsBegan();
            return new SimpleObjectProperty<>(dt != null ? dt.toLocalDate() : null);
        });
        colSeverity.setCellValueFactory(new PropertyValueFactory<>("severity"));
        colExposure.setCellValueFactory(new PropertyValueFactory<>("confirmedExposure"));

        loadCases();

        btnFilter.setOnAction(e -> onFilterClicked());
        btnSearch.setOnAction(e -> onSearchClicked());
        btnSort.setOnAction(e -> onSortClicked());
    }

    private void loadCases() {
        allCases = caseDAO.getAllCases();
        casesTable.setItems(FXCollections.observableArrayList(allCases));
    }

    private void onFilterClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Filter Cases");
        dialog.setHeaderText("Enter severity to filter (Mild, Moderate, Severe):");
        dialog.setContentText("Severity:");

        dialog.showAndWait().ifPresent(input -> {
            String criteria = input.trim();
            Set<String> allowed = new HashSet<>(List.of("Mild", "Moderate", "Severe"));
            if (!allowed.contains(criteria)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid severity");
                alert.showAndWait();
                return;
            }

            List<Case> filtered = allCases.stream()
                    .filter(c -> c.getSeverity().equalsIgnoreCase(criteria))
                    .toList();
            casesTable.setItems(FXCollections.observableArrayList(filtered));
        });
    }

    private void onSearchClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Cases");
        dialog.setHeaderText("Enter CaseID or UserID:");
        dialog.setContentText("ID:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int id = Integer.parseInt(input.trim());
                Map<Integer, Case> caseMap = allCases.stream()
                        .collect(Collectors.toMap(Case::getCaseID, c -> c));
                Case result = caseMap.get(id);
                if (result != null) {
                    casesTable.setItems(FXCollections.observableArrayList(result));
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "No case found for ID " + id);
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid number");
                alert.showAndWait();
            }
        });
    }

    private void onSortClicked() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("DateReported", "DateReported", "Severity", "UserID");
        dialog.setTitle("Sort Cases");
        dialog.setHeaderText("Choose a parameter to sort by:");

        dialog.showAndWait().ifPresent(choice -> {
            List<Case> sorted = new ArrayList<>(allCases);
            switch (choice) {
                case "DateReported" -> sorted.sort(Comparator.comparing(Case::getDateReported));
                case "Severity" -> sorted.sort(Comparator.comparing(Case::getSeverity));
                case "UserID" -> sorted.sort(Comparator.comparing(Case::getUserID));
            }
            casesTable.setItems(FXCollections.observableArrayList(sorted));
        });
    }
}
