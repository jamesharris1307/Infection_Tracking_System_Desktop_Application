package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Index.UserIndexManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.fxml.FXML;

public class AdministratorDashboardController {

    @FXML private TableColumn<User, User.AccountStatus> colAccountStatus;
    @FXML private TableColumn<User, Void> colDisableEnableAction;
    @FXML private TableColumn<User, Void> colDeleteAction;
    @FXML private TableColumn<User, String> colFirstName;
    @FXML private TableColumn<User, String> colLastName;
    @FXML private TableColumn<User, Void> colEditAction;
    @FXML private TableColumn<User, Integer> colCaseId;
    @FXML private TableColumn<User, User.Role> colRole;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableView<User> casesTable;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private TextField txtSearch;
    @FXML private Button logoutButton;
    @FXML private StackPane editOverlay;

    @FXML private TextField fieldFirstName;
    @FXML private TextField fieldLastName;
    @FXML private TextField fieldEmail;
    @FXML private DatePicker fieldDob;
    @FXML private TextField fieldAddress1;
    @FXML private TextField fieldAddress2;
    @FXML private TextField fieldCity;
    @FXML private TextField fieldCounty;
    @FXML private TextField fieldPostcode;
    @FXML private PasswordField fieldPassword;
    @FXML private PasswordField fieldConfirmPassword;

    private User editingUser;

    private final UserIndexManager indexManager = new UserIndexManager();
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML public void initialize() {
        colCaseId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getUserId()).asObject());
        colFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colRole.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRole()));
        colAccountStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAccountStatus()));

        addButtonToTable(colEditAction, "Edit", this::handleEditUser);
        addButtonToTable(colDisableEnableAction, "Toggle", this::handleToggleUserStatus);
        addButtonToTable(colDeleteAction, "Delete", this::handleDeleteUser);

        cmbFilter.getItems().addAll("All", "Active", "Disabled");
        cmbFilter.getSelectionModel().select("All");
        logoutButton.setOnAction(e -> handleLogout());

        if (userService != null) {
            loadUsers();
        } else {
            ExceptionHandler.handle(new IllegalStateException("UserService Not Initialized"),
                    "CRITICAL: UserService Missing From AdministratorController");
        }
    }

    @FXML private void openEditOverlay() {
        editOverlay.setManaged(true);
        editOverlay.setVisible(true);
    }

    @FXML private void closeEditOverlay() {
        editOverlay.setVisible(false);
        editOverlay.setManaged(false);
    }

    private void loadUsers() {
        userService.getAllUsersAsync()
                .thenAccept(users -> Platform.runLater(() -> {
                    userList.setAll(users);
                    casesTable.setItems(userList);
                    indexManager.rebuildIndexes(users);
                }))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Failed to Load Users Asynchronously");
                    return null;
                });
    }

    private void addButtonToTable(TableColumn<User, Void> column, String buttonText, java.util.function.Consumer<User> action) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                button.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (user == null) {
                        ExceptionHandler.handle(
                                ExceptionFactory.validationError("Invalid User Selected From Table"),
                                "Table Action Failed due to Invalid Selection"
                        );
                    } else {
                        action.accept(user);
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    private void handleEditUser(User user) {
        editingUser = user;
        populateEditForm(user);
        openEditOverlay();
    }

    private void populateEditForm(User user) {
        fieldFirstName.setText(user.getFirstName());
        fieldLastName.setText(user.getLastName());
        fieldEmail.setText(user.getEmail());
        fieldDob.setValue(user.getDateOfBirth());
        fieldAddress1.setText(user.getAddressLine1());
        fieldAddress2.setText(user.getAddressLine2());
        fieldCity.setText(user.getTownCity());
        fieldCounty.setText(user.getCounty());
        fieldPostcode.setText(user.getPostcode());

        fieldPassword.clear();
        fieldConfirmPassword.clear();
    }

    private void handleToggleUserStatus(User user) {
        user.setAccountStatus(user.getAccountStatus() == User.AccountStatus.Active
                ? User.AccountStatus.Disabled
                : User.AccountStatus.Active);

        userService.updateUserAsync(user, user.getEmail())
                .thenRun(() -> Platform.runLater(casesTable::refresh))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Failed to Update User Status");
                    return null;
                });
    }

    private void handleDeleteUser(User user) {
        userService.deleteUserAsync(user)
                .thenRun(() -> Platform.runLater(() -> userList.remove(user)))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Failed to Delete User");
                    return null;
                });
    }

    private void filterByStatus(User.AccountStatus status) {
        Platform.runLater(() -> {
            userList.setAll(indexManager.getByStatus(status));
            casesTable.setItems(userList);
        });
    }

    @FXML private void onSearchKeyTyped() {
        String prefix = txtSearch.getText().trim().toLowerCase();
        if (prefix.isEmpty()) {
            casesTable.setItems(userList);
        } else {
            casesTable.setItems(FXCollections.observableArrayList(indexManager.getByNamePrefix(prefix)));
        }
    }

    @FXML private void onFilterClicked() {
        String selection = cmbFilter.getValue();
        switch (selection) {
            case "All" -> casesTable.setItems(userList);
            case "Active" -> filterByStatus(User.AccountStatus.Active);
            case "Disabled" -> filterByStatus(User.AccountStatus.Disabled);
        }
    }

    @FXML
    private void handleEditDetailsSubmit() {

        if (editingUser == null) return;

        editingUser.setFirstName(fieldFirstName.getText());
        editingUser.setLastName(fieldLastName.getText());
        editingUser.setEmail(fieldEmail.getText());
        editingUser.setDateOfBirth(fieldDob.getValue());
        editingUser.setAddressLine1(fieldAddress1.getText());
        editingUser.setAddressLine2(fieldAddress2.getText());
        editingUser.setTownCity(fieldCity.getText());
        editingUser.setCounty(fieldCounty.getText());
        editingUser.setPostcode(fieldPostcode.getText());

        String password = fieldPassword.getText();
        String confirm = fieldConfirmPassword.getText();

        if (!password.isEmpty()) {
            if (!password.equals(confirm)) {
                ExceptionHandler.handle(
                        ExceptionFactory.validationError("Passwords do not match"),
                        "Edit User Failed"
                );
                return;
            }
            editingUser.setPassword(password);
        }

        userService.updateUserAsync(editingUser, editingUser.getEmail())
                .thenRun(() -> Platform.runLater(() -> {
                    casesTable.refresh();
                    closeEditOverlay();
                }))
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    ExceptionHandler.handle(cause, "Failed to Update User");
                    return null;
                });
    }

    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        SceneManager.switchRoot(
                "/com/example/infection_monitoring_system_desktop_application/View/Login.fxml"
        );
    }
}