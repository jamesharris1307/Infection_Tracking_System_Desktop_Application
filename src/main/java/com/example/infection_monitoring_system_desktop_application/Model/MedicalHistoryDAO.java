package com.example.infection_monitoring_system_desktop_application.Model;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.DAOException;
import javax.sql.DataSource;
import java.sql.*;

public class MedicalHistoryDAO {

    private static final String INSERT_HISTORY_SQL = """
        INSERT INTO MedicalHistory (UserID, longTermConditions, longTermMedications, vaccinationUpToDate, allergies, lastUpdated) 
        VALUES (?, ?, ?, ?, ?, ?)
    """;
    private static final String UPDATE_HISTORY_SQL = """
        UPDATE MedicalHistory SET
        longTermConditions = ?,
        longTermMedications = ?,
        vaccinationUpToDate = ?,
        allergies = ?,
        lastUpdated = ?
        WHERE UserID = ?
    """;
    private static final String SELECT_HISTORY_BY_USER_ID_SQL = "SELECT * FROM MedicalHistory WHERE UserID = ?";

    private final DataSource dataSource;

    private MedicalHistory mapResultSetToMedicalHistory(ResultSet rs, int userId) throws SQLException {
        return new MedicalHistory(
                userId,
                rs.getBoolean("longTermConditions"),
                rs.getBoolean("longTermMedications"),
                rs.getBoolean("vaccinationUpToDate"),
                rs.getBoolean("allergies"),
                rs.getTimestamp("lastUpdated").toLocalDateTime()
        );
    }

    public MedicalHistoryDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addMedicalHistory(MedicalHistory medicalHistory) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_HISTORY_SQL)) {

            pstmt.setInt(1, medicalHistory.getUserID());
            pstmt.setBoolean(2, medicalHistory.isLongTermConditions());
            pstmt.setBoolean(3, medicalHistory.isLongTermMedications());
            pstmt.setBoolean(4, medicalHistory.isVaccinationUpToDate());
            pstmt.setBoolean(5, medicalHistory.isAllergies());
            pstmt.setTimestamp(6, Timestamp.valueOf(medicalHistory.getLastUpdated()));
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Failed to add medical history for userID: " + medicalHistory.getUserID(), e);
        }
    }

    public void updateMedicalHistory(MedicalHistory medicalHistory) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_HISTORY_SQL)) {

            pstmt.setBoolean(1, medicalHistory.isLongTermConditions());
            pstmt.setBoolean(2, medicalHistory.isLongTermMedications());
            pstmt.setBoolean(3, medicalHistory.isVaccinationUpToDate());
            pstmt.setBoolean(4, medicalHistory.isAllergies());
            pstmt.setTimestamp(5, Timestamp.valueOf(medicalHistory.getLastUpdated()));
            pstmt.setInt(6, medicalHistory.getUserID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update medical history for userID: " + medicalHistory.getUserID(), e);
        }
    }

    public MedicalHistory getMedicalHistoryByUserId(int userId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_HISTORY_BY_USER_ID_SQL)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMedicalHistory(rs, userId);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to fetch medical history for userID: " + userId, e);
        }
        return null;
    }
}