package com.example.infection_monitoring_system_desktop_application.Service;

import com.example.infection_monitoring_system_desktop_application.Manager.ConcurrencyManager;
import com.example.infection_monitoring_system_desktop_application.Index.UserIndexManager;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import java.util.concurrent.CompletableFuture;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Set;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final UserIndexManager indexManager = new UserIndexManager();

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
            userDAO.updateUser(user, originalEmail);
            getAllUsersAsync().thenAccept(u -> {});
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Void> deleteUserAsync(User user) {
        return CompletableFuture.runAsync(() -> {
            userDAO.deleteUser(user.getEmail());
            indexManager.removeFromIndexes(user);
        }, ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<List<User>> getUsersByStatusAsync(User.AccountStatus status) {
        return CompletableFuture.supplyAsync(() -> indexManager.getByStatus(status),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<List<User>> getUsersByNamePrefixAsync(String prefix) {
        return CompletableFuture.supplyAsync(() -> indexManager.getByNamePrefix(prefix),
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<Set<User>> getUsersSortedByNameAsync() {
        return CompletableFuture.supplyAsync(indexManager::getNameSortedIndex,
                ConcurrencyManager.getExecutor());
    }

    public CompletableFuture<User> getUserByEmailAsync(String email) {
        return CompletableFuture.supplyAsync(() -> userDAO.getUserByEmail(email),
                ConcurrencyManager.getExecutor());
    }
}
