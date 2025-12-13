package com.example.infection_monitoring_system_desktop_application.Unit.Model;

import com.example.infection_monitoring_system_desktop_application.Model.DatabaseConnection;
import com.example.infection_monitoring_system_desktop_application.Model.GeneralPublicUser;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import java.sql.PreparedStatement;
import org.mockito.MockedStatic;
import java.sql.Connection;
import java.time.LocalDate;
import org.mockito.Mockito;
import java.sql.ResultSet;

class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach void setUp() {
        userDAO = new UserDAO();
    }

    @Test void testAddUserSuccess() throws Exception {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeUpdate()).thenReturn(1);

        GeneralPublicUser user = new GeneralPublicUser(
                "test@example.com",
                PasswordUtils.hashPassword("password123"),
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "",
                "City",
                "County",
                "POST123",
                User.AccountStatus.Active
        );

        try (MockedStatic<DatabaseConnection> mockedStatic = Mockito.mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConn);

            userDAO.addUser(user);

            verify(mockStmt).setString(1, user.getEmail());
            verify(mockStmt).setString(2, user.getPassword());
            verify(mockStmt).setString(3, user.getFirstName());
            verify(mockStmt).setString(4, user.getLastName());
            verify(mockStmt).setObject(5, user.getDateOfBirth());
            verify(mockStmt).setString(6, user.getAddressLine1());
            verify(mockStmt).setString(7, user.getAddressLine2());
            verify(mockStmt).setString(8, user.getTownCity());
            verify(mockStmt).setString(9, user.getCounty());
            verify(mockStmt).setString(10, user.getPostcode());
            verify(mockStmt).setString(11, "Active");
            verify(mockStmt).setString(12, "GeneralPublic");
            verify(mockStmt).executeUpdate();
            verify(mockStmt).close();
        }
    }

    @Test void testLoginUserSuccess() throws Exception {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        String hashedPassword = PasswordUtils.hashPassword("password123");

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("Email")).thenReturn("test@example.com");
        when(mockRs.getString("Password")).thenReturn(hashedPassword);
        when(mockRs.getString("FirstName")).thenReturn("John");
        when(mockRs.getString("LastName")).thenReturn("Doe");
        when(mockRs.getDate("DateOfBirth")).thenReturn(java.sql.Date.valueOf("1990-01-01"));
        when(mockRs.getString("AddressLine1")).thenReturn("123 Main St");
        when(mockRs.getString("AddressLine2")).thenReturn("");
        when(mockRs.getString("TownCity")).thenReturn("City");
        when(mockRs.getString("County")).thenReturn("County");
        when(mockRs.getString("Postcode")).thenReturn("POST123");
        when(mockRs.getString("AccountStatus")).thenReturn("Active");
        when(mockRs.getString("Role")).thenReturn("GeneralPublic");

        try (MockedStatic<DatabaseConnection> mockedStatic = Mockito.mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConn);

            User user = userDAO.getUserByEmail("test@example.com");

            assertNotNull(user);
            assertEquals("John", user.getFirstName());
            assertEquals(User.Role.GeneralPublic, user.getRole());
            assertTrue(PasswordUtils.checkPassword("password123", user.getPassword()));
        }
    }

    @Test void loginUser_failWrongPassword() throws Exception {
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        String hashedPassword = PasswordUtils.hashPassword("password123");

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString("Email")).thenReturn("test@example.com");
        when(mockRs.getString("Password")).thenReturn(hashedPassword);
        when(mockRs.getString("Role")).thenReturn("GeneralPublic");
        when(mockRs.getString("FirstName")).thenReturn("John");
        when(mockRs.getString("LastName")).thenReturn("Doe");
        when(mockRs.getDate("DateOfBirth")).thenReturn(java.sql.Date.valueOf("1990-01-01"));
        when(mockRs.getString("AddressLine1")).thenReturn("123 Main St");
        when(mockRs.getString("AddressLine2")).thenReturn("");
        when(mockRs.getString("TownCity")).thenReturn("City");
        when(mockRs.getString("County")).thenReturn("County");
        when(mockRs.getString("Postcode")).thenReturn("POST123");
        when(mockRs.getString("AccountStatus")).thenReturn("Active");

        try (MockedStatic<DatabaseConnection> mockedStatic = Mockito.mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConn);

            User user = userDAO.getUserByEmail("test@example.com");

            assertNotNull(user);
            assertFalse(PasswordUtils.checkPassword("wrongpassword", user.getPassword()));
        }
    }
}