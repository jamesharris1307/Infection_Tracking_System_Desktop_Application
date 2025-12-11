package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import javafx.collections.ObservableList;

import java.util.concurrent.CompletableFuture;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public CompletableFuture<Void> addUserAsync(User user) {
        return CompletableFuture.runAsync(() -> userDAO.addUser(user),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<User> getUserByEmailAsync(String email) {
        return CompletableFuture.supplyAsync(() -> userDAO.getUserByEmail(email),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> updateUserAsync(User user, String originalEmail) {
        return CompletableFuture.runAsync(() -> userDAO.updateUser(user, originalEmail),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<ObservableList<User>> getAllUsersAsync() {
        return CompletableFuture.supplyAsync(userDAO::getAllUsers,
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> deleteUserAsync(String email) {
        return CompletableFuture.runAsync(() -> userDAO.deleteUser(email),
                ConcurrencyManager.getExecutor());
    }
}
