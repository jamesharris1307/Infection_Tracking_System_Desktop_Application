package com.example.infection_monitoring_system_desktop_application.Unit.Model;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.DAOException;
import com.example.infection_monitoring_system_desktop_application.DataAccessObject.MedicalHistoryDAO;
import com.example.infection_monitoring_system_desktop_application.Model.MedicalHistory;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import org.mockito.Mock;
import java.sql.*;

class MedicalHistoryDAOTest {

    @Mock private DataSource mockDataSource;

    @Mock private Connection mockConn;

    @Mock private PreparedStatement mockStmt;

    @Mock private ResultSet mockRs;

    @InjectMocks private MedicalHistoryDAO medicalHistoryDAO;

    private final int TEST_USER_ID = 50;
    private final LocalDateTime TEST_TIME = LocalDateTime.now();
    private MedicalHistory testHistory;

    @BeforeEach void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(mockDataSource.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        testHistory = new MedicalHistory(
                TEST_USER_ID,
                true,
                false,
                true,
                true,
                TEST_TIME
        );
    }

    @Test void testAddMedicalHistory_Success() throws Exception {
        when(mockStmt.executeUpdate()).thenReturn(1);

        medicalHistoryDAO.addMedicalHistory(testHistory);

        verify(mockStmt).setInt(1, TEST_USER_ID);
        verify(mockStmt).setBoolean(2, true);
        verify(mockStmt).setTimestamp(6, Timestamp.valueOf(TEST_TIME));
        verify(mockStmt).executeUpdate();
        verify(mockConn).close();
    }

    @Test void testAddMedicalHistory_ThrowsDAOExceptionOnSqlError() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("DB Insert failed"));

        assertThrows(DAOException.class, () -> medicalHistoryDAO.addMedicalHistory(testHistory));
        verify(mockConn).close();
    }

    @Test void testUpdateMedicalHistory_Success() throws Exception {
        when(mockStmt.executeUpdate()).thenReturn(1);

        medicalHistoryDAO.updateMedicalHistory(testHistory);

        verify(mockStmt).setBoolean(1, true);
        verify(mockStmt).setTimestamp(5, Timestamp.valueOf(TEST_TIME));
        verify(mockStmt).setInt(6, TEST_USER_ID);
        verify(mockStmt).executeUpdate();
    }


    @Test void testGetMedicalHistory_Success_Found() throws Exception {
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        when(mockRs.getBoolean("longTermConditions")).thenReturn(testHistory.isLongTermConditions());
        when(mockRs.getBoolean("longTermMedications")).thenReturn(testHistory.isLongTermMedications());
        when(mockRs.getBoolean("vaccinationUpToDate")).thenReturn(testHistory.isVaccinationUpToDate());
        when(mockRs.getBoolean("allergies")).thenReturn(testHistory.isAllergies());
        when(mockRs.getTimestamp("lastUpdated")).thenReturn(Timestamp.valueOf(TEST_TIME));

        MedicalHistory retrievedHistory = medicalHistoryDAO.getMedicalHistoryByUserId(TEST_USER_ID);

        assertNotNull(retrievedHistory);
        assertEquals(TEST_USER_ID, retrievedHistory.getUserID());
        assertTrue(retrievedHistory.isLongTermConditions());

        verify(mockStmt).setInt(1, TEST_USER_ID);
        verify(mockRs).close();
    }

    @Test void testGetMedicalHistory_NotFound() throws Exception {
        when(mockStmt.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

        MedicalHistory retrievedHistory = medicalHistoryDAO.getMedicalHistoryByUserId(TEST_USER_ID);

        assertNull(retrievedHistory);
    }
}