package com.example.infection_monitoring_system_desktop_application.Model;

import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UserDAO {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void addUserAsync(User user, Runnable onSuccess, Consumer<Exception> onError) {

        executor.submit(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {

                addUser(user, conn);

                Platform.runLater(onSuccess);

            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        });
    }

    private void addUser(User user, Connection conn) throws SQLException {

        String sql = "INSERT INTO Users (Email, Password, FirstName, LastName, DateOfBirth, " +
                "AddressLine1, AddressLine2, TownCity, County, Postcode, AccountStatus, Role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setObject(5, user.getDateOfBirth());
            pstmt.setString(6, user.getAddressLine1());
            pstmt.setString(7, user.getAddressLine2());
            pstmt.setString(8, user.getTownCity());
            pstmt.setString(9, user.getCounty());
            pstmt.setString(10, user.getPostcode());
            pstmt.setString(11, user.getAccountStatus().name());
            pstmt.setString(12, user.getRole().name());

            pstmt.executeUpdate();
        }
    }
}
