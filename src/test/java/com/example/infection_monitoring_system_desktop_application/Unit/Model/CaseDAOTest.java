package com.example.infection_monitoring_system_desktop_application.Unit.Model;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.DAOException;
import com.example.infection_monitoring_system_desktop_application.Model.GeneralPublicUser;
import com.example.infection_monitoring_system_desktop_application.Model.CaseDAO;
import com.example.infection_monitoring_system_desktop_application.Model.Case;
import com.example.infection_monitoring_system_desktop_application.Model.User;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import org.mockito.InjectMocks;
import javax.sql.DataSource;
import org.mockito.Mock;
import java.util.List;
import java.util.Set;
import java.sql.*;

class CaseDAOTest {

    @Mock private DataSource mockDataSource;

    @Mock private Connection mockConn;

    @InjectMocks private CaseDAO caseDAO;

    @Mock private PreparedStatement mockStmt;
    @Mock private ResultSet mockRs;

    private final LocalDateTime REPORT_TIME = LocalDateTime.now().minusHours(2);
    private final LocalDateTime SYMPTOMS_BEGAN = LocalDateTime.now().minusDays(1);
    private final Set<String> SYMPTOMS = Set.of("Fever", "Cough");

    private final Case testCase = new Case(
            101,
            REPORT_TIME,
            SYMPTOMS_BEGAN,
            SYMPTOMS,
            "Moderate",
            true
    );

    @BeforeEach void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(mockDataSource.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        doNothing().when(mockRs).close();
    }

    @Test void testAddCaseSuccess() throws Exception {
        when(mockStmt.executeUpdate()).thenReturn(1);

        caseDAO.addCase(testCase);

        verify(mockStmt).setInt(1, testCase.getUserID());
        verify(mockStmt).setTimestamp(2, Timestamp.valueOf(REPORT_TIME));
        verify(mockStmt).setTimestamp(3, Timestamp.valueOf(SYMPTOMS_BEGAN));
        verify(mockStmt).setString(4, String.join(",", SYMPTOMS));
        verify(mockStmt).setString(5, "Moderate");
        verify(mockStmt).setBoolean(6, true);

        verify(mockStmt).executeUpdate();
        verify(mockConn).close();
        verify(mockStmt).close();
    }

    @Test void testAddCase_SymptomsBeganIsNullSuccess() throws Exception {
        Case caseWithoutOnset = new Case(102, REPORT_TIME, null, SYMPTOMS, "Mild", false);
        when(mockStmt.executeUpdate()).thenReturn(1);

        caseDAO.addCase(caseWithoutOnset);

        verify(mockStmt).setTimestamp(3, null);

        verify(mockStmt).executeUpdate();
        verify(mockConn).close();
        verify(mockStmt).close();
    }

    @Test void testAddCase_InvalidSeverity() {
        Case invalidCase = new Case(101, REPORT_TIME, SYMPTOMS_BEGAN, SYMPTOMS, "Critical", true);

        assertThrows(IllegalArgumentException.class, () -> caseDAO.addCase(invalidCase));

        verifyNoInteractions(mockConn);
    }

    @Test void testAddCase_InvalidSymptom() {
        Set<String> badSymptoms = Set.of("Fever", "Nausea");
        Case invalidCase = new Case(101, REPORT_TIME, SYMPTOMS_BEGAN, badSymptoms, "Moderate", true);

        assertThrows(IllegalArgumentException.class, () -> caseDAO.addCase(invalidCase));

        verifyNoInteractions(mockConn);
    }

    @Test void testAddCase_ShouldThrowDAOExceptionOnSqlError() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Insert failed"));

        assertThrows(DAOException.class, () -> caseDAO.addCase(testCase));
        verify(mockConn).close();
    }

    @Test void testGetAllCasesSuccess_TwoCases() throws Exception {
        User user1 = new GeneralPublicUser("user1@example.com", "hash", "Alice", "A", null, "Addr", "", "Town", "County", "Post", User.AccountStatus.Active);
        User user2 = new GeneralPublicUser("user2@example.com", "hash", "Bob", "B", null, "Addr", "", "Town", "County", "Post", User.AccountStatus.Active);

        LocalDateTime time1 = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime time2 = LocalDateTime.of(2025, 1, 2, 10, 0);

        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, true, false);

        when(mockRs.getInt("CaseID")).thenReturn(1).thenReturn(2);
        when(mockRs.getInt("UserID")).thenReturn(101).thenReturn(102);
        when(mockRs.getTimestamp("DateReported"))
                .thenReturn(Timestamp.valueOf(time1))
                .thenReturn(Timestamp.valueOf(time2));
        when(mockRs.getTimestamp("SymptomsBegan"))
                .thenReturn(null) // Case 1: Null onset
                .thenReturn(Timestamp.valueOf(time2.minusDays(1)));
        when(mockRs.getString("Symptoms"))
                .thenReturn("Fever,Cough")
                .thenReturn("Headache");
        when(mockRs.getString("Severity"))
                .thenReturn("Mild")
                .thenReturn("Severe");
        when(mockRs.getBoolean("ConfirmedExposure"))
                .thenReturn(true)
                .thenReturn(false);

        when(mockRs.getString("FirstName"))
                .thenReturn(user1.getFirstName())
                .thenReturn(user2.getFirstName());
        when(mockRs.getString("LastName"))
                .thenReturn(user1.getLastName())
                .thenReturn(user2.getLastName());
        when(mockRs.getString("Email"))
                .thenReturn(user1.getEmail())
                .thenReturn(user2.getEmail());

        List<Case> cases = caseDAO.getAllCases();

        assertEquals(2, cases.size());

        Case c1 = cases.get(0);
        assertEquals(1, c1.getCaseID());
        assertEquals(time1, c1.getDateReported());
        assertNull(c1.getSymptomsBegan());
        assertTrue(c1.getSymptoms().contains("Fever"));
        assertEquals("Mild", c1.getSeverity());
        assertEquals("Alice", c1.getUser().getFirstName());

        Case c2 = cases.get(1);
        assertEquals(2, c2.getCaseID());
        assertEquals("Severe", c2.getSeverity());
        assertEquals("Bob", c2.getUser().getFirstName());
        assertNotNull(c2.getSymptomsBegan());

        verify(mockStmt).executeQuery();
        verify(mockRs, times(3)).next();
        verify(mockConn).close();
        verify(mockStmt).close();
        verify(mockRs).close();
    }

    @Test void testGetAllCases_ShouldThrowDAOExceptionOnSqlError() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Select all failed"));

        assertThrows(DAOException.class, () -> caseDAO.getAllCases());
        verify(mockConn).close();
    }
}