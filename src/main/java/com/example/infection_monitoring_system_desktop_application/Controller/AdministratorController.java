package com.example.infection_monitoring_system_desktop_application.Controller;

import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Service.UserService;
import com.example.infection_monitoring_system_desktop_application.Util.AlertUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdministratorController {

    @FXML
    private TableView<User> casesTable;

    @FXML
    private TableColumn<User, Integer> colCaseId;
    @FXML
    private TableColumn<User, String> colFirstName;
    @FXML
    private TableColumn<User, String> colLastName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, User.Role> colRole;
    @FXML
    private TableColumn<User, User.AccountStatus> colAccountStatus;
    @FXML
    private TableColumn<User, Void> colEditAction;
    @FXML
    private TableColumn<User, Void> colDisableEnableAction;
    @FXML
    private TableColumn<User, Void> colDeleteAction;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        colCaseId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getUserId()).asObject());
        colFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        colLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colRole.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getRole()));
        colAccountStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getAccountStatus()));

        addButtonToTable(colEditAction, "Edit", this::handleEditUser);
        addButtonToTable(colDisableEnableAction, "Toggle", this::handleToggleUserStatus);
        addButtonToTable(colDeleteAction, "Delete", this::handleDeleteUser);

        loadUsers();
    }

    private void loadUsers() {
        userService.getAllUsersAsync()
                .thenAccept(users -> Platform.runLater(() -> {
                    userList.setAll(users);
                    casesTable.setItems(userList);
                }))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> AlertUtils.showError("Error", "Failed to load users."));
                    return null;
                });
    }

    private void addButtonToTable(TableColumn<User, Void> column, String buttonText, java.util.function.Consumer<User> action) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                button.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    action.accept(user);
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
        user.setAccountStatus(user.getAccountStatus() == User.AccountStatus.Active
                ? User.AccountStatus.Disabled
                : User.AccountStatus.Active);

        userService.updateUserAsync(user, user.getEmail())
                .thenRun(() -> Platform.runLater(casesTable::refresh))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> AlertUtils.showError("Error", "Failed to update user."));
                    return null;
                });
    }

    private void handleDeleteUser(User user) {
        userService.deleteUserAsync(user.getEmail())
                .thenRun(() -> Platform.runLater(() -> userList.remove(user)))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> AlertUtils.showError("Error", "Failed to delete user."));
                    return null;
                });
    }
}
