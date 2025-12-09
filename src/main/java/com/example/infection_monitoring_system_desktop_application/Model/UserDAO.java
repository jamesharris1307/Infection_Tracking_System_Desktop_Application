package com.example.infection_monitoring_system_desktop_application.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String roleStr = rs.getString("Role");
                User.Role role = User.Role.valueOf(roleStr);

                switch (role) {
                    case GeneralPublic:
                        return new GeneralPublicUser(
                                rs.getString("Email"),
                                rs.getString("Password"),
                                rs.getString("FirstName"),
                                rs.getString("LastName"),
                                rs.getDate("DateOfBirth").toLocalDate(),
                                rs.getString("AddressLine1"),
                                rs.getString("AddressLine2"),
                                rs.getString("TownCity"),
                                rs.getString("County"),
                                rs.getString("Postcode"),
                                User.AccountStatus.valueOf(rs.getString("AccountStatus"))
                        );
                    case HealthcareProfessional:
                        return new HealthcareProfessionalUser(
                                rs.getString("Email"),
                                rs.getString("Password"),
                                rs.getString("FirstName"),
                                rs.getString("LastName"),
                                rs.getDate("DateOfBirth").toLocalDate(),
                                rs.getString("AddressLine1"),
                                rs.getString("AddressLine2"),
                                rs.getString("TownCity"),
                                rs.getString("County"),
                                rs.getString("Postcode"),
                                User.AccountStatus.valueOf(rs.getString("AccountStatus"))
                        );
                    case Administrator:
                        return new AdministratorUser(
                                rs.getString("Email"),
                                rs.getString("Password"),
                                rs.getString("FirstName"),
                                rs.getString("LastName"),
                                rs.getDate("DateOfBirth").toLocalDate(),
                                rs.getString("AddressLine1"),
                                rs.getString("AddressLine2"),
                                rs.getString("TownCity"),
                                rs.getString("County"),
                                rs.getString("Postcode"),
                                User.AccountStatus.valueOf(rs.getString("AccountStatus"))
                        );
                    default:
                        throw new IllegalStateException("Unknown role: " + roleStr);
                }
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateUser(User user, String originalEmail) {
        String sql = "UPDATE Users SET " +
                "FirstName = ?, " +
                "LastName = ?, " +
                "Email = ?, " +
                "Password = ?, " +
                "DateOfBirth = ?, " +
                "AddressLine1 = ?, " +
                "AddressLine2 = ?, " +
                "TownCity = ?, " +
                "County = ?, " +
                "Postcode = ? " +
                "WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setObject(5, user.getDateOfBirth());
            pstmt.setString(6, user.getAddressLine1());
            pstmt.setString(7, user.getAddressLine2());
            pstmt.setString(8, user.getTownCity());
            pstmt.setString(9, user.getCounty());
            pstmt.setString(10, user.getPostcode());
            pstmt.setString(11, originalEmail);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String email) {
        String sql = "DELETE FROM Users WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}