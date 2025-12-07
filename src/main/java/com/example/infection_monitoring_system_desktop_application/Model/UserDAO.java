package com.example.infection_monitoring_system_desktop_application.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {

    public void addUser(User user) {
        String sql = "INSERT INTO Users (Email, Password, FirstName, LastName, DateOfBirth, " +
                "AddressLine1, AddressLine2, TownCity, County, Postcode, AccountStatus, Role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
