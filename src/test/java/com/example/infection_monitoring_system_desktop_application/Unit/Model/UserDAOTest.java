package com.example.infection_monitoring_system_desktop_application.Unit.Model;

import com.example.infection_monitoring_system_desktop_application.Model.GeneralPublicUser;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import com.example.infection_monitoring_system_desktop_application.Model.UserDAO;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

class UserDAOTest {

    @Mock
    private DataSource mockDataSource;

    @Mock
    private Connection mockConn;

    @InjectMocks
    private UserDAO userDAO;

    @Mock
    private PreparedStatement mockStmt;
    @Mock
    private ResultSet mockRs;


    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(mockDataSource.getConnection()).thenReturn(mockConn);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
    }

    @Test
    void testAddUserSuccess() throws Exception {

        when(mockStmt.executeUpdate()).thenReturn(1);

        GeneralPublicUser user = new GeneralPublicUser(
                "test@example.com",
                PasswordUtils.hashPassword("password123"),
                "John", "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St", "", "City", "County", "POST123",
                User.AccountStatus.Active
        );

        userDAO.addUser(user);

        verify(mockDataSource, times(1)).getConnection();
        verify(mockConn, times(1)).close();

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
        verify(mockStmt).setString(11, user.getAccountStatus().name());
        verify(mockStmt).setString(12, user.getRole().name());

        verify(mockStmt).executeUpdate();
        verify(mockStmt).close();
    }

    @Test
    void testGetUserByEmailSuccess() throws Exception {

        String hashedPassword = PasswordUtils.hashPassword("password123");

        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);

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

        User user = userDAO.getUserByEmail("test@example.com");

        assertNotNull(user);
        assertEquals("John", user.getFirstName());
        assertEquals(User.Role.GeneralPublic, user.getRole());
        assertEquals(User.AccountStatus.Active, user.getAccountStatus());

        assertTrue(PasswordUtils.checkPassword("password123", user.getPassword()));
        verify(mockConn, times(1)).close();
    }

    @Test
    void loginUser_failUserNotFound() throws Exception {

        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // No user found

        User user = userDAO.getUserByEmail("missing@example.com");

        assertNull(user);
        verify(mockConn, times(1)).close();
    }
}