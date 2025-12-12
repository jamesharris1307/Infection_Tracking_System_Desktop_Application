package com.example.infection_monitoring_system_desktop_application.Model;

import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.DAOException;

import java.sql.*;

public class MedicalHistoryDAO {

    public void addMedicalHistory(MedicalHistory medicalHistory) {
        String sql = "INSERT INTO MedicalHistory " +
                "(UserID, longTermConditions, longTermMedications, vaccinationUpToDate, allergies, lastUpdated) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, medicalHistory.getUserID());
            pstmt.setBoolean(2, medicalHistory.isLongTermConiditions());
            pstmt.setBoolean(3, medicalHistory.isLongTermMedications());
            pstmt.setBoolean(4, medicalHistory.isVaccinationUpToDate());
            pstmt.setBoolean(5, medicalHistory.isAllergies());
            pstmt.setObject(6, medicalHistory.getLastUpdated());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to add medical history for userID: " + medicalHistory.getUserID(), e);
        }
    }

    public void updateMedicalHistory(MedicalHistory medicalHistory) {
        String sql = "UPDATE MedicalHistory SET " +
                "longTermConditions = ?, " +
                "longTermMedications = ?, " +
                "vaccinationUpToDate = ?, " +
                "allergies = ?, " +
                "lastUpdated = ? " +
                "WHERE UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, medicalHistory.isLongTermConiditions());
            pstmt.setBoolean(2, medicalHistory.isLongTermMedications());
            pstmt.setBoolean(3, medicalHistory.isVaccinationUpToDate());
            pstmt.setBoolean(4, medicalHistory.isAllergies());
            pstmt.setObject(5, medicalHistory.getLastUpdated());
            pstmt.setInt(6, medicalHistory.getUserID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Failed to update medical history for userID: " + medicalHistory.getUserID(), e);
        }
    }

    public MedicalHistory getMedicalHistoryByUserId(int userId) {
        String sql = "SELECT * FROM MedicalHistory WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new MedicalHistory(
                        userId,
                        rs.getBoolean("longTermConditions"),
                        rs.getBoolean("longTermMedications"),
                        rs.getBoolean("vaccinationUpToDate"),
                        rs.getBoolean("allergies"),
                        rs.getTimestamp("lastUpdated").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to fetch medical history for userID: " + userId, e);
        }
        return null;
    }
}
