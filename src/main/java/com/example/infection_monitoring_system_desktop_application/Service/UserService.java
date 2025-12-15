package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Index.UserIndexManager;
import com.example.infection_monitoring_system_desktop_application.Util.ExceptionFactory;
import com.example.infection_monitoring_system_desktop_application.Model.DashboardInfo;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import java.util.concurrent.CompletableFuture;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Set;

public class UserService {

    private final UserDAO userDAO;
    private final UserIndexManager indexManager = new UserIndexManager();

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public CompletableFuture<User> loginAsync(String email, String plaintextPassword) {
        return CompletableFuture.supplyAsync(() -> {

            User user = userDAO.getUserByEmail(email);

            if (user == null) {
                throw ExceptionFactory.userNotFound(email);
            }

            if (user.getAccountStatus() != User.AccountStatus.Active) {
                throw ExceptionFactory.validationError("Account is not active. Please contact support.");
            }

            if (!PasswordUtils.checkPassword(plaintextPassword, user.getPassword())) {
                throw ExceptionFactory.invalidPassword();
            }

            return user;

        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<User> registerUserAsync(User newUser) {

        if (newUser == null) {
            throw ExceptionFactory.validationError("Cannot register a null user object.");
        }

        return CompletableFuture.supplyAsync(() -> {

            String hashedPassword = PasswordUtils.hashPassword(newUser.getPassword());
            newUser.setPassword(hashedPassword);

            if (newUser.getRole() == User.Role.GeneralPublic) {
                newUser.setAccountStatus(User.AccountStatus.Active);
            } else {
                newUser.setAccountStatus(User.AccountStatus.Disabled);
            }

            userDAO.addUser(newUser);
            indexManager.addToIndexes(newUser);

            return newUser;

        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<ObservableList<User>> getAllUsersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            ObservableList<User> users = userDAO.getAllUsers();
            indexManager.rebuildIndexes(users);
            return users;
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> addUserAsync(User user) {
        return CompletableFuture.runAsync(() -> {
            userDAO.addUser(user);
            indexManager.addToIndexes(user);
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> updateUserAsync(User user, String originalEmail) {
        return CompletableFuture.runAsync(() -> {
            User oldUser = userDAO.getUserByEmail(originalEmail);
            userDAO.updateUser(user, originalEmail);

            if (oldUser != null) {
                indexManager.removeFromIndexes(oldUser);
            }
            indexManager.addToIndexes(user);

        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> deleteUserAsync(User user) {
        return CompletableFuture.runAsync(() -> {
            userDAO.deleteUser(user.getEmail());
            indexManager.removeFromIndexes(user);
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<DashboardInfo> getDashboardInfoAsync(int userId) {
        return CompletableFuture.supplyAsync(() ->
                        userDAO.getDashboardInfoByUserId(userId),
                ConcurrencyManager.getExecutor()
        );
    }
}