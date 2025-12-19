package com.example.infection_monitoring_system_desktop_application.DataAccessObject;

import com.example.infection_monitoring_system_desktop_application.Model.*;
import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.DAOException;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javax.sql.DataSource;
import java.sql.*;

public class UserDAO {

    private static final String INSERT_USER_SQL = """
        INSERT INTO Users (Email, Password, FirstName, LastName, DateOfBirth, 
        AddressLine1, AddressLine2, TownCity, County, Postcode, AccountStatus, Role) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;
    private static final String SELECT_USER_BY_EMAIL_SQL = "SELECT * FROM Users WHERE Email = ?";

    private static final String UPDATE_USER_SQL = """
        UPDATE Users SET 
        FirstName = ?, LastName = ?, Email = ?, 
        Password = ?, DateOfBirth = ?, AddressLine1 = ?, 
        AddressLine2 = ?, TownCity = ?, County = ?, 
        Postcode = ?, AccountStatus = ? 
        WHERE Email = ?
    """;

    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM Users";

    private static final String DELETE_USER_SQL = "DELETE FROM Users WHERE Email = ?";

    private final DataSource dataSource;

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int userId = rs.getInt("UserID");
        String roleStr = rs.getString("Role");
        User.Role role = User.Role.valueOf(roleStr);

        User user;
        String email = rs.getString("Email");
        String password = rs.getString("Password");
        String firstName = rs.getString("FirstName");
        String lastName = rs.getString("LastName");
        java.time.LocalDate dob = rs.getDate("DateOfBirth").toLocalDate();
        String addr1 = rs.getString("AddressLine1");
        String addr2 = rs.getString("AddressLine2");
        String town = rs.getString("TownCity");
        String county = rs.getString("County");
        String postcode = rs.getString("Postcode");
        User.AccountStatus status = User.AccountStatus.valueOf(rs.getString("AccountStatus"));

        switch (role) {
            case GeneralPublic:
                user = new GeneralPublicUser(email, password, firstName, lastName, dob, addr1, addr2, town, county, postcode, status);
                break;
            case HealthcareProfessional:
                user = new HealthcareProfessionalUser(email, password, firstName, lastName, dob, addr1, addr2, town, county, postcode, status);
                break;
            case Administrator:
                user = new AdministratorUser(email, password, firstName, lastName, dob, addr1, addr2, town, county, postcode, status);
                break;
            default:
                throw new IllegalStateException("Unknown role: " + roleStr);
        }
        user.setUserId(userId);
        return user;
    }

    public UserDAO(DataSource ds) {
        if (ds == null) {
            throw new IllegalArgumentException("DataSource must be provided to UserDAO.");
        }
        this.dataSource = ds;
    }

    public void addUser(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_SQL)) {

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
        } catch (SQLException e) { throw new DAOException("Failed to add user: " + user.getEmail(), e); }
    }

    public User getUserByEmail(String email) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_USER_BY_EMAIL_SQL)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { return mapResultSetToUser(rs);}
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Failed to get user by email: " + email, e);
        }
    }

    public void updateUser(User user, String originalEmail) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_USER_SQL)) {

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
            pstmt.setString(11, user.getAccountStatus().name());
            pstmt.setString(12, originalEmail);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update user: " + originalEmail, e);
        }
    }

    public ObservableList<User> getAllUsers() {
        ObservableList<User> userList = FXCollections.observableArrayList();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_USERS_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                userList.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            throw new DAOException("Failed to get all users", e);
        }

        return userList;
    }

    public void deleteUser(String email) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_USER_SQL)) {

            pstmt.setString(1, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to delete user: " + email, e);
        }
    }

    public DashboardInfo getDashboardInfoByUserId(int userId) {
        String sql = """
        SELECT u.Email, u.FirstName, u.LastName, u.DateOfBirth,
               mh.LastUpdated, mh.LongTermConditions, mh.VaccinationUpToDate
        FROM Users u
        LEFT JOIN MedicalHistory mh ON u.UserID = mh.UserID
        WHERE u.UserID = ?
        ORDER BY mh.LastUpdated DESC
        LIMIT 1
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DashboardInfo info = new DashboardInfo();
                    info.setEmail(rs.getString("Email"));
                    info.setFirstName(rs.getString("FirstName"));
                    info.setLastName(rs.getString("LastName"));
                    info.setDateOfBirth(rs.getDate("DateOfBirth").toLocalDate());

                    Date lastUpdated = rs.getDate("LastUpdated");
                    if (lastUpdated != null) {
                        info.setLastHistoryUpdate(lastUpdated.toLocalDate());
                    }

                    info.setExistingConditions(rs.getString("LongTermConditions"));
                    info.setVaccinationStatus(rs.getString("VaccinationUpToDate"));

                    return info;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Failed to get Dashboard Info for User: " + userId, e);
        }
    }
}