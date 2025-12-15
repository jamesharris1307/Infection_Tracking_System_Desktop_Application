package com.example.infection_monitoring_system_desktop_application.Unit.Model;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.DAOException;
import com.example.infection_monitoring_system_desktop_application.Util.PasswordUtils;
import com.example.infection_monitoring_system_desktop_application.Model.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import javax.sql.DataSource;
import java.time.LocalDate;
import org.mockito.Mock;
import java.sql.*;

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

    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD_HASH = PasswordUtils.hashPassword("password123");
    private final GeneralPublicUser testUser = new GeneralPublicUser(
            TEST_EMAIL,
            TEST_PASSWORD_HASH,
            "John", "Doe",
            LocalDate.of(1990, 1, 1),
            "123 Main St", "", "City", "County", "POST123",
            User.AccountStatus.Active
    );

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(mockDataSource.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        doNothing().when(mockRs).close();
    }

    private void mockSuccessfulResultSet(User userToMock) throws SQLException {
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true);

        when(mockRs.getInt("UserID")).thenReturn(1);
        when(mockRs.getString("Email")).thenReturn(userToMock.getEmail());
        when(mockRs.getString("Password")).thenReturn(userToMock.getPassword());
        when(mockRs.getString("FirstName")).thenReturn(userToMock.getFirstName());
        when(mockRs.getString("LastName")).thenReturn(userToMock.getLastName());
        when(mockRs.getDate("DateOfBirth")).thenReturn(java.sql.Date.valueOf(userToMock.getDateOfBirth()));
        when(mockRs.getString("AddressLine1")).thenReturn(userToMock.getAddressLine1());
        when(mockRs.getString("AddressLine2")).thenReturn(userToMock.getAddressLine2());
        when(mockRs.getString("TownCity")).thenReturn(userToMock.getTownCity());
        when(mockRs.getString("County")).thenReturn(userToMock.getCounty());
        when(mockRs.getString("Postcode")).thenReturn(userToMock.getPostcode());
        when(mockRs.getString("AccountStatus")).thenReturn(userToMock.getAccountStatus().name());
        when(mockRs.getString("Role")).thenReturn(userToMock.getRole().name());
    }

    @Test
    void testAddUserSuccess() throws Exception {
        when(mockStmt.executeUpdate()).thenReturn(1);

        userDAO.addUser(testUser);

        verify(mockDataSource).getConnection();
        verify(mockConn).prepareStatement(anyString());
        verify(mockStmt).executeUpdate();
        verify(mockConn).close();
        verify(mockStmt).close();

        verify(mockStmt).setString(1, testUser.getEmail());
        verify(mockStmt).setString(2, testUser.getPassword());
        verify(mockStmt).setString(11, testUser.getAccountStatus().name());
        verify(mockStmt).setString(12, testUser.getRole().name());
    }

    @Test
    void testAddUser_ShouldThrowDAOExceptionOnSqlError() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Insert failed"));

        assertThrows(DAOException.class, () -> userDAO.addUser(testUser));
        verify(mockConn).close();
    }

    @Test
    void testGetUserByEmailSuccess() throws Exception {
        mockSuccessfulResultSet(testUser);

        User user = userDAO.getUserByEmail(TEST_EMAIL);

        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals(User.Role.GeneralPublic, user.getRole());
        assertInstanceOf(GeneralPublicUser.class, user);

        verify(mockStmt).setString(1, TEST_EMAIL);
        verify(mockStmt).executeQuery();
        verify(mockRs, times(1)).next();
        verify(mockConn).close();
        verify(mockStmt).close();
        verify(mockRs).close();
    }

    @Test
    void testGetUserByEmail_NotFound() throws Exception {
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false); // No user found

        User user = userDAO.getUserByEmail("missing@example.com");

        assertNull(user);
        verify(mockStmt).setString(1, "missing@example.com");
        verify(mockConn).close();
    }

    @Test
    void testGetUserByEmail_ShouldThrowDAOExceptionOnSqlError() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Query failed"));

        assertThrows(DAOException.class, () -> userDAO.getUserByEmail(TEST_EMAIL));
        verify(mockConn).close();
    }


    @Test
    void testUpdateUserSuccess() throws Exception {
        when(mockStmt.executeUpdate()).thenReturn(1);
        String originalEmail = "old@email.com";

        GeneralPublicUser updatedUser = new GeneralPublicUser(
                "new@email.com",
                TEST_PASSWORD_HASH,
                "Jane", "Smith",
                testUser.getDateOfBirth(),
                testUser.getAddressLine1(), "", testUser.getTownCity(), testUser.getCounty(), testUser.getPostcode(),
                User.AccountStatus.Disabled
        );

        userDAO.updateUser(updatedUser, originalEmail);

        verify(mockStmt).setString(1, "Jane");
        verify(mockStmt).setString(3, "new@email.com");
        verify(mockStmt).setString(11, User.AccountStatus.Disabled.name());
        verify(mockStmt).setString(12, originalEmail);

        verify(mockStmt).executeUpdate();
        verify(mockConn).close();
        verify(mockStmt).close();
    }

    @Test
    void testUpdateUser_ShouldThrowDAOExceptionOnSqlError() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Update failed"));

        assertThrows(DAOException.class, () -> userDAO.updateUser(testUser, TEST_EMAIL));
        verify(mockConn).close();
    }

    @Test
    void testGetAllUsersSuccess_TwoUsers() throws Exception {
        AdministratorUser adminUser = new AdministratorUser(
                "admin@example.com", TEST_PASSWORD_HASH, "Boss", "Man",
                LocalDate.of(1980, 1, 1), "1 Admin Rd", "", "HQ", "Zone", "ADMIN1",
                User.AccountStatus.Active
        );

        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, true, false);

        when(mockRs.getString("UserID"))
                .thenReturn("1")
                .thenReturn("2");

        when(mockRs.getString("Role"))
                .thenReturn(testUser.getRole().name())
                .thenReturn(adminUser.getRole().name());

        when(mockRs.getInt("UserID"))
                .thenReturn(1)
                .thenReturn(2);

        when(mockRs.getString("Email"))
                .thenReturn(testUser.getEmail())
                .thenReturn(adminUser.getEmail());

        when(mockRs.getString("Password"))
                .thenReturn(testUser.getPassword())
                .thenReturn(adminUser.getPassword());

        when(mockRs.getString("FirstName"))
                .thenReturn(testUser.getFirstName())
                .thenReturn(adminUser.getFirstName());

        when(mockRs.getString("LastName"))
                .thenReturn(testUser.getLastName())
                .thenReturn(adminUser.getLastName());

        when(mockRs.getDate("DateOfBirth"))
                .thenReturn(java.sql.Date.valueOf(testUser.getDateOfBirth()))
                .thenReturn(java.sql.Date.valueOf(adminUser.getDateOfBirth()));

        when(mockRs.getString("AddressLine1"))
                .thenReturn(testUser.getAddressLine1())
                .thenReturn(adminUser.getAddressLine1());

        when(mockRs.getString("AddressLine2"))
                .thenReturn(testUser.getAddressLine2())
                .thenReturn(adminUser.getAddressLine2());

        when(mockRs.getString("TownCity"))
                .thenReturn(testUser.getTownCity())
                .thenReturn(adminUser.getTownCity());

        when(mockRs.getString("County"))
                .thenReturn(testUser.getCounty())
                .thenReturn(adminUser.getCounty());

        when(mockRs.getString("Postcode"))
                .thenReturn(testUser.getPostcode())
                .thenReturn(adminUser.getPostcode());

        when(mockRs.getString("AccountStatus"))
                .thenReturn(testUser.getAccountStatus().name())
                .thenReturn(adminUser.getAccountStatus().name());

        int userCount = userDAO.getAllUsers().size();
        assertEquals(2, userCount);
        verify(mockStmt).executeQuery();
        verify(mockRs, times(3)).next();
        verify(mockConn).close();
        verify(mockStmt).close();
        verify(mockRs).close();
    }

    @Test
    void testGetAllUsers_ShouldThrowDAOExceptionOnSqlError() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Select all failed"));

        assertThrows(DAOException.class, () -> userDAO.getAllUsers());
        verify(mockConn).close();
    }

    @Test
    void testDeleteUserSuccess() throws Exception {
        when(mockStmt.executeUpdate()).thenReturn(1);

        userDAO.deleteUser(TEST_EMAIL);

        verify(mockStmt).setString(1, TEST_EMAIL);
        verify(mockStmt).executeUpdate();
        verify(mockConn).close();
        verify(mockStmt).close();
    }

    @Test
    void testDeleteUser_ShouldThrowDAOExceptionOnSqlError() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Delete failed"));

        assertThrows(DAOException.class, () -> userDAO.deleteUser(TEST_EMAIL));
        verify(mockConn).close();
    }

    @Test
    void constructor_ShouldThrowExceptionWhenDataSourceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserDAO(null));
    }
}