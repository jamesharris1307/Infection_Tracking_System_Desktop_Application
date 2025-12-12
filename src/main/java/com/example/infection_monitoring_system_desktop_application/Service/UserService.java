package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    private final Map<User.AccountStatus, List<User>> statusIndex = new HashMap<>();

    private void rebuildStatusIndex(ObservableList<User> users) {
        statusIndex.clear();
        for (User u : users) {
            statusIndex
                    .computeIfAbsent(u.getAccountStatus(), k -> new ArrayList<>())
                    .add(u);
        }
    }

    public CompletableFuture<Void> addUserAsync(User user) {
        return CompletableFuture.runAsync(() -> {
            userDAO.addUser(user);
            statusIndex
                    .computeIfAbsent(user.getAccountStatus(), k -> new ArrayList<>())
                    .add(user);
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<User> getUserByEmailAsync(String email) {
        return CompletableFuture.supplyAsync(() -> userDAO.getUserByEmail(email),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> updateUserAsync(User user, String originalEmail) {
        return CompletableFuture.runAsync(() -> {
            userDAO.updateUser(user, originalEmail);
            getAllUsersAsync().thenAccept(this::rebuildStatusIndex);
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<ObservableList<User>> getAllUsersAsync() {
        return CompletableFuture.supplyAsync(() -> {
            ObservableList<User> users = userDAO.getAllUsers();
            rebuildStatusIndex(users);
            return users;
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> deleteUserAsync(String email) {
        return CompletableFuture.runAsync(() -> {
            userDAO.deleteUser(email);
            getAllUsersAsync().thenAccept(this::rebuildStatusIndex);
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<List<User>> getUsersByStatusAsync(User.AccountStatus status) {
        return CompletableFuture.supplyAsync(() ->
                        statusIndex.getOrDefault(status, Collections.emptyList()),
                ConcurrencyManager.getExecutor());
    }
}
