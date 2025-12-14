package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Index.UserIndexManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
import com.example.infection_monitoring_system_desktop_application.Manager.SessionManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionHandler;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
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

    private final UserIndexManager indexManager = new UserIndexManager();
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML public void initialize() {
        try {
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
                ExceptionHandler.handle(new IllegalStateException("UserService dependency not initialized."),
                        "CRITICAL: UserService is missing in Dashboard.");
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error initializing AdministratorController");
        }
    }

    private void setupButtons() {
        logoutButton.setOnAction(e -> handleLogout());
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
                    ExceptionHandler.handle(cause, "Failed to load users asynchronously");
                    return null;
                });
    }

    private void addButtonToTable(TableColumn<User, Void> column, String buttonText, java.util.function.Consumer<User> action) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button(buttonText);
            {
                button.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    try {
                        if (user == null) throw ExceptionFactory.validationError("Invalid user selected.");
                        action.accept(user);
                    } catch (Exception e) {
                        ExceptionHandler.handle(e, "Error performing table action");
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    private void handleEditUser(User user) {
        System.out.println("Editing user: " + user.getUserId());
    }

    private void handleToggleUserStatus(User user) {
        try {
            user.setAccountStatus(user.getAccountStatus() == User.AccountStatus.Active
                    ? User.AccountStatus.Disabled
                    : User.AccountStatus.Active);

            userService.updateUserAsync(user, user.getEmail())
                    .thenRun(() -> Platform.runLater(casesTable::refresh))
                    .exceptionally(ex -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        ExceptionHandler.handle(cause, "Failed to update user status");
                        return null;
                    });
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error toggling user status");
        }
    }

    private void handleDeleteUser(User user) {
        try {
            userService.deleteUserAsync(user)
                    .thenRun(() -> Platform.runLater(() -> userList.remove(user)))
                    .exceptionally(ex -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        ExceptionHandler.handle(cause, "Failed to delete user");
                        return null;
                    });
        } catch (Exception e) {
            ExceptionHandler.handle(e, "Error deleting user");
        }
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

    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        SceneManager.switchRoot(
                "/com/example/infection_monitoring_system_desktop_application/View/Login.fxml"
        );
    }
}